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

    val fontBitmapSizes = intArrayOf(32, 24, 16)

    private val fonts = mutableMapOf<FontSpecification, BasicFont>()
    val defaultFont: BasicFont

    private val validFonts = mutableSetOf<String>()

    init {
        registerFont("Arial", 16)
        defaultFont = fonts.get(FontSpecification("Arial", Font.PLAIN, 16))!!
    }

    fun font(spec: FontSpecification): BasicFont? {
        return fonts.get(spec)
    }

    /**
     * divide in-font size by float result to get actual size
     * multiply actual size by float result to get in-font size
     */
    fun font(fontName: String, style: Int, targetSize: Int): Pair<BasicFont, Float> {
        if(fontName !in validFonts) {
            LibrarianLog.warn("Asked for font '$fontName' that isn't registered! Register it using `FontLoader.registerFont(\"$fontName\")`")
            return Pair(defaultFont, 16f/targetSize)
        }

        var fontFound: BasicFont? = null
        var maxSize = 0
        var foundStyle = false

        for((spec, font) in fonts.entries) {
            if(spec.font == fontName) {

                if(!foundStyle && spec.style == 0 && (spec.resolution >= targetSize && maxSize < spec.resolution)) {
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

        fontFound ?: return Pair(defaultFont, 16f/targetSize)

        return Pair(fontFound, maxSize.toFloat()/targetSize)
    }

    /**
     * font size is divided by [shadowDist] to get the shadow offset
     */
    fun registerFont(font: String, shadowDist: Int) {
        validFonts.add(font)

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
        fonts.put(spec, bitmap)
    }
}

private class FileHandleInputStream(val stream: InputStream) : FileHandleStream("foobar") {
    override fun read() = stream
}

data class FontSpecification(val font: String, val style: Int, val resolution: Int)