package com.teamwizardry.librarianlib.client.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandleStream
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.MathUtils
import com.sun.tools.doclets.formats.html.resources.standard
import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.core.libgdxhax.MyPixmapPacker
import java.io.InputStream

/**
 * Created by TheCodeWarrior
 */
object FontLoader {

    val fontBitmapSizes = intArrayOf(32, 24, 16)

    private val fonts = mutableMapOf<FontSpecification, BitmapFont>()

    init {
        loadFonts(EnumFont.HELVETICA, EnumFontStyle.NORMAL)
        loadFonts(EnumFont.UNIFONT, EnumFontStyle.NORMAL)
    }

    fun getFont(family: EnumFont, style: EnumFontStyle, targetSize: Int): Pair<BitmapFont?, Float> {
        var fontFound: BitmapFont? = null
        var maxSize = 0
        var foundStyle = false

        for((spec, font) in fonts.entries) {
            if(spec.family == family) {

                if(!foundStyle && spec.style == EnumFontStyle.NORMAL && (spec.resolution >= targetSize && maxSize < spec.resolution)) {
                    fontFound = font
                    maxSize = spec.resolution
                }

                if(spec.style == style) {
                    if(!foundStyle) {
                        foundStyle = true
                        maxSize = 0
                    }
                    if(spec.resolution >= targetSize && maxSize < spec.resolution) {
                        fontFound = font
                        maxSize = spec.resolution
                    }
                }
            }
        }

        return Pair(fontFound, maxSize.toFloat()/targetSize)
    }

    private fun loadFonts(family: EnumFont, style: EnumFontStyle) {
        val stream = LibrarianLib::class.java.getResourceAsStream("/assets/librarianlib/font/${family.name.toLowerCase()}/${style.name.toLowerCase()}.ttf")
        val generator = FreeTypeFontGenerator(FileHandleInputStream(stream))

        for(size in fontBitmapSizes) {
            loadFont(FontSpecification(family, style, size), generator)
        }
    }

    private fun loadFont(specification: FontSpecification, generator: FreeTypeFontGenerator) {
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.color = Color.RED
        parameter.shadowColor = Color.GREEN
        parameter.flip = true
        parameter.incremental = true
        parameter.magFilter = Texture.TextureFilter.Linear
        parameter.minFilter = Texture.TextureFilter.Linear

        parameter.size = specification.resolution
        parameter.shadowOffsetX = specification.resolution/8
        parameter.shadowOffsetY = specification.resolution/8

        val data = FreeTypeFontGenerator.FreeTypeBitmapFontData()
        val font = generator.generateFont(parameter, data)

        val packer = LLFontRendererMethodHandles.get_FreeTypeBitmapFontData_packer(data)
        val newPacker = MyPixmapPacker(packer, LLFontRendererMethodHandles.get_PixmapPacker_packStrategy(packer))
        LLFontRendererMethodHandles.set_FreeTypeBitmapFontData_packer(data, newPacker)

        fonts[specification] = font
    }

}

private class FileHandleInputStream(val stream: InputStream) : FileHandleStream("foobar") {
    override fun read() = stream
}

private data class FontSpecification(val family: EnumFont, val style: EnumFontStyle, val resolution: Int)

enum class EnumFont {
    HELVETICA, UNIFONT;
}

enum class EnumFontStyle {
    NORMAL, ITALIC, BOLD, BOLD_ITALIC
}