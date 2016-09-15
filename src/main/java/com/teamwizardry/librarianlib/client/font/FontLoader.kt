package com.teamwizardry.librarianlib.client.font

import com.badlogic.gdx.files.FileHandleStream
import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.fx.shader.ShaderProgram
import net.minecraft.util.ResourceLocation
import org.newdawn.slick.TrueTypeFont
import java.awt.Font
import java.io.InputStream

/**
 * Created by TheCodeWarrior
 */
object FontLoader {

    val bitmapFont: BitmapFont
    val fontBitmapSizes = intArrayOf(32, 24, 16)

    private val fonts = mutableMapOf<FontSpecification, TrueTypeFont>()

    init {
        bitmapFont = BitmapFont(Font("Arial", Font.PLAIN, 16), true)
        loadFonts(EnumFont.HELVETICA, EnumFontStyle.NORMAL)
        loadFonts(EnumFont.UNIFONT, EnumFontStyle.NORMAL)
    }

    fun getFont(family: EnumFont, style: EnumFontStyle, targetSize: Int): Pair<TrueTypeFont?, Float> {
        var fontFound: TrueTypeFont? = null
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

//        val generator = FreeTypeFontGenerator(FileHandleInputStream(stream))

        for(size in fontBitmapSizes) {
            loadFont(FontSpecification(family, style, size), stream)
        }
    }

    private fun loadFont(specification: FontSpecification, stream: InputStream) {

        try {

            var awtFont = Font.createFont(Font.TRUETYPE_FONT, stream)
            awtFont = awtFont.deriveFont(specification.resolution) // set font size
            val font = TrueTypeFont(awtFont, true)

            fonts[specification] = font
        } catch (e: Exception) {
            e.printStackTrace()
        }
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