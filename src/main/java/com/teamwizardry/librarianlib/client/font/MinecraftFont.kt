package com.teamwizardry.librarianlib.client.font

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResource
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import sun.awt.image.DataBufferNative
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.*
import java.io.Closeable
import java.io.IOException

/**
 * Created by TheCodeWarrior
 */
open class MinecraftFont(val spec: FontSpecification) : PackedFont() {

    private val UNICODE_PAGE_LOCATIONS = arrayOfNulls<ResourceLocation>(256)

    /** Array of the start/end column (in upper/lower nibble) for every glyph in the /font directory. */
    protected val glyphWidth = ByteArray(65536)

    private val whitespace = intArrayOf(
            0x0009, 0x000A, 0x000B, 0x000C, 0x000D,
            0x0020, 0x0085, 0x00A0, 0x1680, 0x2000,
            0x2001, 0x2002, 0x2003, 0x2004, 0x2005,
            0x2006, 0x2007, 0x2008, 0x2009, 0x200A,
            0x2028, 0x2029, 0x202F, 0x205F, 0x3000
    )

    private val pageTextures = mutableMapOf<Int, BufferedImage>()

    init {

        var iresource: IResource? = null

        try {
            iresource = getResource(ResourceLocation("font/glyph_sizes.bin"))
            iresource.inputStream.read(this.glyphWidth)
        } catch (ioexception: IOException) {
            throw RuntimeException(ioexception)
        } finally {
            IOUtils.closeQuietly(iresource as Closeable?)
        }

        postInit()
    }

    protected fun getPage(c: Int): BufferedImage {

        val page = c / 256

        var iresource: IResource? = null
        val bufferedimage: BufferedImage

        try {
            iresource = getResource(getUnicodePageLocation(page))
            bufferedimage = TextureUtil.readBufferedImage(iresource.inputStream)
        } catch (ioexception: IOException) {
            throw RuntimeException(ioexception)
        } finally {
            IOUtils.closeQuietly(iresource as Closeable?)
        }

//        for(x in 0..(bufferedimage.width-1)) {
//            for(y in 0..(bufferedimage.height-1)) {
//                bufferedimage.setRGB(x, y, 0)
//            }
//        }

        val img = BufferedImage(bufferedimage.width, bufferedimage.height, BufferedImage.TYPE_INT_ARGB)

        for(x in 0..(img.width-1)) {
            for(y in 0..(img.height-1)) {
                val a = bufferedimage.getRGB(x,y) shr 24 and 0xff
                if(a != 0) {
                    img.setRGB(x, y, a shl 24)
                }
            }
        }

        return img
    }

    private fun getUnicodePageLocation(page: Int): ResourceLocation {
        var v = UNICODE_PAGE_LOCATIONS[page]
        if (v == null) {
            v = ResourceLocation(String.format("textures/font/unicode_page_%02x.png", Integer.valueOf(page)))
            UNICODE_PAGE_LOCATIONS[page] = v
        }
        return v
    }

    protected fun glyphX(c: Int): Float {
        val page = c/256
        val posInPage = c - page*256

        val xIndex = posInPage % 16
        return xIndex / 16f
    }

    protected fun glyphY(c: Int): Float {
        val page = c/256
        val posInPage = c - page*256

        val yIndex = posInPage / 16
        return yIndex / 16f
    }

    // =================================================================================================================

    @Throws(IOException::class)
    protected fun getResource(location: ResourceLocation): IResource {
        return Minecraft.getMinecraft().resourceManager.getResource(location)
    }

    override fun getShear(): Float = 0f
    override fun getStyle(): Int = 0

    override fun withStyle(style: Int): BasicFont {
        if(style == Font.ITALIC)
            return FontLoader.unifont_italic
        if(style == Font.BOLD)
            return FontLoader.unifont_bold
        if(style == Font.BOLD or Font.ITALIC)
            return FontLoader.unifont_bold_italic
        return this
    }

    override fun getAdvance(c: Int): Int {
        if(c == 32)
            return 4

        if(c >= 65536)
            return 0
        val i = glyphWidth[c].toInt()
        if(i == 0)
            return 0
        return (i and 0x0F) - (i and 0xF0 shr 4) + 2
    }

    override fun isWhitespace(c: Int): Boolean {
        return c in whitespace
    }

    override fun drawToGraphcs(c: Int, g: Graphics2D, image: BufferedImage) {
        if(c >= 65536)
            return

        val pageimage = getPage(c)
        val subimage = pageimage.getSubimage((glyphX(c)*pageimage.width).toInt(), (glyphY(c)*pageimage.height).toInt(), pageimage.width/16, pageimage.height/16)

        val red = BufferedImage(subimage.width, subimage.height, BufferedImage.TYPE_INT_ARGB)
        val green = BufferedImage(subimage.width, subimage.height, BufferedImage.TYPE_INT_ARGB)

        val oH = -(glyphWidth[c].toInt() and 0xF0 shr 4)+1
        val oV = -16+2

        val sH = 1
        val sV = 1

        for(x in 0..(subimage.width-1)) {
            for(y in 0..(subimage.height-1)) {
                val a = subimage.getRGB(x,y) shr 24 and 0xff

                if(a != 0) {
                    image.setRGB(x+32+oH+sH, y+32+oV+sV, a shl 24 or 0x00FF00)
                }
            }
        }

        for(x in 0..(subimage.width-1)) {
            for(y in 0..(subimage.height-1)) {
                val a = subimage.getRGB(x,y) shr 24 and 0xff

                if(a != 0) {
                    image.setRGB(x+32+oH, y+32+oV, a shl 24 or 0xFF0000)
                }
            }
        }
    }

    override fun fontMapExportLoc(): String {
        return "font_unifont"
    }
}

open class BoldMinecraftFont(spec: FontSpecification) : MinecraftFont(spec) {

    override fun getAdvance(c: Int): Int {
        return super.getAdvance(c) + 1
    }

    override fun drawToGraphcs(c: Int, g: Graphics2D, image: BufferedImage) {
        if(c >= 65536)
            return

        val pageimage = getPage(c)
        val subimage = pageimage.getSubimage((glyphX(c)*pageimage.width).toInt(), (glyphY(c)*pageimage.height).toInt(), pageimage.width/16, pageimage.height/16)

        val red = BufferedImage(subimage.width, subimage.height, BufferedImage.TYPE_INT_ARGB)
        val green = BufferedImage(subimage.width, subimage.height, BufferedImage.TYPE_INT_ARGB)

        val oH = -(glyphWidth[c].toInt() and 0xF0 shr 4)+1
        val oV = -16+2

        val sH = 1
        val sV = 1

        for(x in 0..(subimage.width-1)) {
            for(y in 0..(subimage.height-1)) {
                val a = subimage.getRGB(x,y) shr 24 and 0xff

                if(a != 0) {
                    image.setRGB(x+32+oH+sH, y+32+oV+sV, a shl 24 or 0x00FF00)
                    image.setRGB(x+32+oH+sH+1, y+32+oV+sV, a shl 24 or 0x00FF00)
                }
            }
        }

        for(x in 0..(subimage.width-1)) {
            for(y in 0..(subimage.height-1)) {
                val a = subimage.getRGB(x,y) shr 24 and 0xff

                if(a != 0) {
                    image.setRGB(x+32+oH, y+32+oV, a shl 24 or 0xFF0000)
                    image.setRGB(x+32+oH+1, y+32+oV, a shl 24 or 0xFF0000)
                }
            }
        }
    }

    override fun fontMapExportLoc(): String {
        return "font_unifont_bold"
    }
}

class ItalicMinecraftFont(val font: MinecraftFont) : BasicFont() {
    override fun getShear(): Float = 1f/8f

    override fun getStyle(): Int = Font.ITALIC or font.getStyle()

    override fun withStyle(style: Int): BasicFont {
        return font.withStyle(style)
    }

    override fun getGlyph(c: Int): Glyph {
        return font.getGlyph(c)
    }
}