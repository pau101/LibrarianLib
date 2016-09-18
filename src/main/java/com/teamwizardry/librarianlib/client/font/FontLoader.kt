package com.teamwizardry.librarianlib.client.font

import com.badlogic.gdx.files.FileHandleStream
import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.fx.shader.ShaderProgram
import net.minecraft.util.ResourceLocation
import org.newdawn.slick.TrueTypeFont
import java.awt.Font
import java.io.InputStream

/**
 * Created by TheCodeWarrior
 */
object FontLoader {

    val unifont: MinecraftFont
    val unifont_italic: ItalicMinecraftFont
    val unifont_bold: BoldMinecraftFont
    val unifont_bold_italic: ItalicMinecraftFont

    val fontBitmapSizes = intArrayOf(32, 24, 16)

    private val fonts = mutableMapOf<FontSpecification, BasicFont>()
    val defaultFont: BasicFont

    private val validFonts = mutableSetOf<String>()

    init {
        registerFont("Arial", 16)

        val uniSpec = FontSpecification("Unifont", Font.PLAIN, 16)
        val uniSpecBold = FontSpecification("Unifont", Font.BOLD, 16)
        val uniSpecItalic = FontSpecification("Unifont", Font.ITALIC, 16)
        val uniSpecBoldItalic = FontSpecification("Unifont", Font.BOLD or Font.ITALIC, 16)

        val unifont = MinecraftFont(uniSpec)
        val unifont_bold = BoldMinecraftFont(uniSpecBold)
        val unifont_italic = ItalicMinecraftFont(unifont)
        val unifont_bold_italic = ItalicMinecraftFont(unifont_bold)

        registerFont(uniSpec, unifont)
        registerFont(uniSpecBold, unifont_bold)
        registerFont(uniSpecItalic, unifont_italic)
        registerFont(uniSpecBoldItalic, unifont_bold_italic)

        defaultFont = unifont

        this.unifont = unifont
        this.unifont_bold = unifont_bold
        this.unifont_italic = unifont_italic
        this.unifont_bold_italic = unifont_bold_italic
//        defaultFont = fonts.get(FontSpecification("Arial", Font.PLAIN, 16))!!
    }

    fun font(spec: FontSpecification): BasicFont? {
        return fonts.get(spec)
    }

    /**
     * divide in-font size by float result to get actual size
     * multiply actual size by float result to get in-font size
     */
    fun font(fontName: String, style: Int, targetSize: Int): Pair<BasicFont, Float> {
        if(fontName.toLowerCase() !in validFonts) {
            LibrarianLog.warn("Asked for font '$fontName' that isn't registered! Register it using `FontLoader.registerFont(\"$fontName\")`")
            return Pair(defaultFont, 16f/targetSize)
        }

        var fontFound: BasicFont? = null
        var currentSize = 0
        var foundStyle = false

        for((spec, font) in fonts.entries) {
            if(spec.font.equals(fontName, true)) {

                if(!foundStyle && spec.style == 0 && (spec.resolution >= targetSize && currentSize < spec.resolution)) {
                    fontFound = font
                    currentSize = spec.resolution
                }

                if(spec.style == style) {
                    if(!foundStyle) {
                        foundStyle = true
                        currentSize = 0
                    }
                    if(Math.abs(targetSize-spec.resolution) < Math.abs(targetSize-currentSize)) {
                        fontFound = font
                        currentSize = spec.resolution
                    }
                }
            }
        }

        fontFound ?: return Pair(defaultFont, targetSize/16f)

        return Pair(fontFound, targetSize/currentSize.toFloat())
    }

    fun registerFont(spec: FontSpecification, font: BasicFont) {
        validFonts.add(spec.font.toLowerCase())
        fonts.put(spec, font)
    }

    /**
     * font size is divided by [shadowDist] to get the shadow offset
     */
    fun registerFont(font: String, shadowDist: Int) {
        for(size in fontBitmapSizes) {
            loadFont(font, Font.PLAIN, size, size/shadowDist)
            loadFont(font, Font.BOLD, size, size/shadowDist)
            loadFont(font, Font.ITALIC, size, size/shadowDist)
            loadFont(font, Font.BOLD or Font.ITALIC, size, size/shadowDist)
        }
    }

    private fun loadFont(font: String, style: Int, size: Int, shadow: Int) {
        val spec = FontSpecification(font, style, size)
        val bitmap = BitmapFont(spec, Font(font, style, size), true, shadow, shadow)
        registerFont(spec, bitmap)
    }
}

private class FileHandleInputStream(val stream: InputStream) : FileHandleStream("foobar") {
    override fun read() = stream
}

data class FontSpecification(val font: String, val style: Int, val resolution: Int)