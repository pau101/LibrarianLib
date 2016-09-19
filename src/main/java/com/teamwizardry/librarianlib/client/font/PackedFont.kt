package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.client.core.GLTextureExport
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage

/**
 * Created by TheCodeWarrior
 */
abstract class PackedFont : BasicFont() {

    abstract protected fun getAdvance(c: Int): Int
    abstract protected fun drawToGraphcs(c: Int, g: Graphics2D, image: BufferedImage)
    abstract protected fun fontMapExportLoc(): String

    private val whitespace = intArrayOf(
            0x0009, 0x000A, 0x000B, 0x000C, 0x000D,
            0x0020, 0x0085, 0x00A0, 0x1680, 0x2000,
            0x2001, 0x2002, 0x2003, 0x2004, 0x2005,
            0x2006, 0x2007, 0x2008, 0x2009, 0x200A,
            0x2028, 0x2029, 0x202F, 0x205F, 0x3000
    )
    open protected fun isWhitespace(c: Int): Boolean {
        return c in whitespace
    }

    open protected fun canBreakAfter(c: Int): Boolean {
        return c.toChar() in "({[<$#&@*+^/\\="
    }

    open protected fun canBreakBefore(c: Int): Boolean {
        return c.toChar() in ")}]>!?,\"`%&@*+^/\\=-:;"
    }


    protected val glyphs = mutableMapOf<Int, Glyph>()
    protected val glyphMetrics = mutableMapOf<Int, GlyphMetrics>()
    private lateinit var invalidGlyph: Glyph
        private set
    private lateinit var invalidGlyphRect: Rectangle
        private set

    private val queuedGlyphUploads = mutableSetOf<GlyphUpload>()

    private val bufferTextOrigin: Int
    private val textRenderBuffer: BufferedImage
    protected val g: Graphics2D

    private val packer = Packer(64, FontRenderer.maxTextureSize)
    private var textureID: Int
    protected val texture: Texture
    private var textureWidth = 64
    private var textureHeight = 64

    init {
        MinecraftForge.EVENT_BUS.register(this)

        // create render buffer
        val maxFontSize = 32
        val bufferSize = maxFontSize*2
        bufferTextOrigin = maxFontSize

        textRenderBuffer = BufferedImage(bufferSize, bufferSize, BufferedImage.TYPE_INT_ARGB)
        g = textRenderBuffer.graphics as Graphics2D

        // generate texture
        val img = BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB)

        textureID = TextureUtil.glGenTextures()
        TextureUtil.uploadTextureImage(textureID, img)

        var thiz = this

        texture = object : Texture() {
            override val textureID: Int
                get() = thiz.textureID
            override val width: Int
                get() = thiz.textureWidth
            override val height: Int
                get() = thiz.textureHeight
        }
    }

    protected fun postInit() {
        invalidGlyph = getGlyph(0)
        invalidGlyphRect = Rectangle(invalidGlyph.u, invalidGlyph.v, invalidGlyph.width, invalidGlyph.height)
    }

    fun asStr(c: Int): String {
        return String(Character.toChars(c))
    }

    // =================================================================================================================

    override fun getGlyph(c: Int): Glyph {
        return glyphs.getOrPut(c) {
            val str = String(Character.toChars(c))

            // get preliminary info
            val advance = getAdvance(c)

            if (isWhitespace(c)) {
                val info = GlyphMetrics(this, c.toChar(), 0, 0, 0, 0, advance, true, false, true)
                return@getOrPut Glyph(texture, c.toChar(), 0, 0, 0, 0, info)
            }

            // clear the graphics

            g.background = Color(0, 0, 0, 0)
            g.clearRect(0, 0, textRenderBuffer.width, textRenderBuffer.height)

            // render the text
            g.translate(bufferTextOrigin, bufferTextOrigin)

            drawToGraphcs(c, g, textRenderBuffer)

            g.translate(-bufferTextOrigin, -bufferTextOrigin)

            // get the size of the graphics area that contains image data
            val rect = getWrittenArea()

            // create metrics
            val bearingX = rect.x - bufferTextOrigin
            val bearingY = bufferTextOrigin - rect.y
            val width = rect.width
            val height = rect.height

            val glyphInfo = GlyphMetrics(this, c.toChar(), bearingX, bearingY, width, height, advance, false, canBreakBefore(c), canBreakAfter(c))

            this.glyphMetrics.put(c, glyphInfo)

            // create glyph
            val image = textRenderBuffer.getSubimageCopy(rect.x, rect.y, rect.width, rect.height)

            var atlasRect = packer.pack(width, height)
            if (atlasRect != null)
                queuedGlyphUploads.add(GlyphUpload(atlasRect.x, atlasRect.y, image))
            if (atlasRect == null)
                atlasRect = Rectangle(invalidGlyphRect)

            Glyph(texture, c.toChar(), atlasRect.x, atlasRect.y, atlasRect.width, atlasRect.height, glyphInfo)
        }
    }

    // =================================================================================================================

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

    @SubscribeEvent
    fun renderTick(event: TickEvent.RenderTickEvent) {
        uploadQueued()
    }

    private fun uploadQueued() {
        if(packer.width != textureWidth || packer.height != textureHeight) {
            resize()
        }
        for(upload in queuedGlyphUploads) {
            TextureUtil.uploadTextureImageSub(textureID, upload.tex, upload.posX, upload.posY, false, false)
        }
        if(queuedGlyphUploads.size > 0) {
            val loc = fontMapExportLoc()
            if(loc != null) GLTextureExport.saveGlTexture(loc, 1)
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