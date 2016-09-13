package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.core.GLTextureExport
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import sun.font.Font2D
import java.awt.*
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Created by TheCodeWarrior
 */
class BitmapFont(val font: Font, val antiAlias: Boolean) {

    var textureWidth = 64
        private set
    var textureHeight = 64
        private set

    private val glyphs = mutableMapOf<Int, Glyph>()
    private val glyphMetrics = mutableMapOf<Int, GlyphMetrics>()

    private val queuedGlyphUploads = mutableSetOf<GlyphUpload>()

    val metrics: FontMetrics
    val font2d: Font2D

    private val bufferTextOrigin: Int
    private val textRenderBuffer: BufferedImage
    private val g: Graphics2D

    private val packer = Packer(64)
    var textureID: Int
        private set
    init {
        MinecraftForge.EVENT_BUS.register(this)

        font2d = FontMethodHandles.call_Font_getFont2D(font)

        // create render buffer
        val maxFontSize = 32
        val bufferSize = maxFontSize*2
        bufferTextOrigin = maxFontSize

        textRenderBuffer = BufferedImage(bufferSize, bufferSize, BufferedImage.TYPE_INT_ARGB)
        g = textRenderBuffer.graphics as Graphics2D
        g.font = font
        if (antiAlias == true) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON)
        }

        metrics = g.fontMetrics

        // generate texture
        val img = BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB)

        textureID = TextureUtil.glGenTextures()
        TextureUtil.uploadTextureImage(textureID, img)
    }

    fun getGlyph(c: Int): Glyph {
        return glyphs.getOrPut(c) {
            val str = String(Character.toChars(c))

            // get preliminary info
            val awtMetrics = font.createGlyphVector(g.fontRenderContext, str).getGlyphMetrics(0)
            val advance = metrics.charWidth(c)

            if(awtMetrics.isWhitespace) {
                val info = GlyphMetrics(this, c.toChar(), 0, 0, 0, 0, advance)
                return@getOrPut Glyph(c.toChar(), 0, 0, 0, 0, info)
            }

            // clear the graphics

            g.background = Color(0, 0, 0, 0)
            g.clearRect(0,0, textRenderBuffer.width, textRenderBuffer.height)

            // render the text
            g.color = Color.RED
            g.drawString(str, bufferTextOrigin, bufferTextOrigin)

            // get the size of the graphics area that contains image data
            val rect = getWrittenArea()

            // create metrics
            val bearingX = rect.x - bufferTextOrigin
            val bearingY = bufferTextOrigin - rect.y
            val width = rect.width
            val height = rect.height

            val glyphInfo = GlyphMetrics(this, c.toChar(), bearingX, bearingY, width, height, advance)

            this.glyphMetrics.put(c, glyphInfo)

            // create glyph
            val image = textRenderBuffer.getSubimageCopy(rect.x, rect.y, rect.width, rect.height)

            var atlasRect = packer.pack(width, height)
            if(atlasRect != null)
                queuedGlyphUploads.add(GlyphUpload(atlasRect.x, atlasRect.y, image))
            if(atlasRect == null)
                atlasRect = Rectangle(0, 0, width, height)

            Glyph(c.toChar(), atlasRect.x, atlasRect.y, atlasRect.width, atlasRect.height, glyphInfo)
        }
    }

    private fun getWrittenArea(): Rectangle {
        val raster = textRenderBuffer.alphaRaster
        val width = raster.width
        val height = raster.height
        var left = 0
        var top = 0
        var right = width - 1
        var bottom = height - 1
        var minRight = width - 1
        var minBottom = height - 1

        top@ while (top < bottom) {
            for (x in 0..width - 1) {
                if (raster.getSample(x, top, 0) !== 0) {
                    minRight = x
                    minBottom = top
                    break@top
                }
            }
            top++
        }

        left@ while (left < minRight) {
            for (y in height - 1 downTo top + 1) {
                if (raster.getSample(left, y, 0) !== 0) {
                    minBottom = y
                    break@left
                }
            }
            left++
        }

        bottom@ while (bottom > minBottom) {
            for (x in width - 1 downTo left) {
                if (raster.getSample(x, bottom, 0) !== 0) {
                    minRight = x
                    break@bottom
                }
            }
            bottom--
        }

        right@ while (right > minRight) {
            for (y in bottom downTo top) {
                if (raster.getSample(right, y, 0) !== 0) {
                    break@right
                }
            }
            right--
        }

        return Rectangle(left, top, right - left + 1, bottom - top + 1)
    }

    fun getGlyphMetrics(c: Int): GlyphMetrics {
        return getGlyph(c).metrics
    }

    @SubscribeEvent
    fun renderTick(event: TickEvent.RenderTickEvent) {
        uploadQueued()
    }

    fun uploadQueued() {
        if(packer.width != textureWidth || packer.height != textureHeight) {
            resize()
        }
        for(upload in queuedGlyphUploads) {
            TextureUtil.uploadTextureImageSub(textureID, upload.tex, upload.posX, upload.posY, false, false)
        }
        if(queuedGlyphUploads.size > 0) {
            GLTextureExport.saveGlTexture("font", 1)
        }
        queuedGlyphUploads.clear()
    }

    private fun resize() {
        GlStateManager.bindTexture(textureID)

        textureWidth = packer.width
        textureHeight = packer.height

        // existing image
        val width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
        val height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)

        val size = width * height

        val existingImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        val buffer = BufferUtils.createIntBuffer(size)
        val data = IntArray(size)

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer)
        buffer.get(data)
        existingImage.setRGB(0, 0, width, height, data, 0, width)

        // write
        val image = BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB)

        val graphics = image.graphics
        graphics.drawImage(existingImage, 0, 0, null)

        TextureUtil.deleteTexture(textureID)
        textureID = TextureUtil.glGenTextures()
        TextureUtil.uploadTextureImage(textureID, image)
    }

}

data class Glyph(val c: Char, val u: Int, val v: Int, val width: Int, val height: Int, val metrics: GlyphMetrics)

data class GlyphMetrics(val font: BitmapFont, val c: Char, val bearingX: Int, val bearingY: Int, val width: Int, val height: Int, val advance: Int)

data class GlyphUpload(val posX: Int, val posY: Int, val tex: BufferedImage)

fun BufferedImage.getSubimageCopy(x: Int, y: Int, w: Int, h: Int): BufferedImage {
    // Get subimage "normal way"
    val subimage = this.getSubimage(x, y, w, h)

    // Create new empty image of same type
    val subcopy = BufferedImage(w, h, this.getType())

    // Draw the subimage onto the new, empty copy
    val g = subcopy.createGraphics()
    try {
        g.drawImage(subimage, 0, 0, null)
    } finally {
        g.dispose()
    }

    return subcopy
}