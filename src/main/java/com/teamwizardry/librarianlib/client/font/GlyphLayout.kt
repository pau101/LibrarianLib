package com.teamwizardry.librarianlib.client.font

import com.apple.laf.resources.aqua
import com.teamwizardry.librarianlib.LibrarianLog
import org.apache.commons.io.IOUtils
import java.awt.Color
import java.awt.Font
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class GlyphLayout() {

    var wrap: Int = -1

    var text: String = ""
    var format: TextFormatting = TextFormatting("Unifont", Font.PLAIN, 16)
    var fontName: String = "Unifont"
    var fontSize: Int = 16
    var fontStyle: Int = Font.PLAIN

    fun layoutGlyphs(): List<GlyphDrawInfo> {
        val format_ = TextFormatting(fontName, fontStyle, fontSize)
        format_.text = format.text
        format_.strikethrough = format.strikethrough
        format_.shadow = format.shadow
        format_.underline = format.underline

        var format = format_.clone()
        val str = text

        val lineHeight = fontSize

        var x = 0f
        var y = 0f

        val list = ArrayList<GlyphDrawInfo>(str.length)

        var i = 0
        var lastWordBreak = -1
        var lastBreakOpportunity = -1

        while(i < str.length) {
            val c = str[i]

            if(c == '§') {
                val len = validFormatLength(str, i+1)
                if(len == 0) {
                    i++ // increase once so i is at the next char. at the end of the loop i is incremented past it
                } else {
                    i++ // increase i so it's after the section sign
                    val formatStr = str.substring(i, i+len)
                    i += len

                    format = handleFormat(formatStr, format, format_)
                    continue
                }
            }

            val glyph = format.font.getGlyph(c.toInt())
            val advance = ( glyph.metrics.advance * format.scale ).toInt()

            if(wrap >= 0 && ( x + advance > wrap && !glyph.metrics.isWhitespace)) { // all the wrapping code. That's it. :D
                var breakPos = -1 // the glyph at this index and any after it will be moved down a line.
                if(lastWordBreak >= 0) {
                    breakPos = lastWordBreak+1
                }
                else if(lastBreakOpportunity >= 0) {
                    breakPos = lastBreakOpportunity
                }

                if(breakPos > 0) {
                    if(breakPos < list.size) {
                        var glyphIndex = breakPos
                        val xOffset = list[glyphIndex].x

                        while(glyphIndex < list.size) {
                            list[glyphIndex].x -= xOffset
                            list[glyphIndex].y += lineHeight
                            glyphIndex++
                        }
                        x -= xOffset
                    } else {
                        x = 0f
                    }
                    y += lineHeight

                    if(lastWordBreak <= breakPos)
                        lastWordBreak = -1
                    if(lastBreakOpportunity <= breakPos)
                        lastBreakOpportunity = -1
                } else {
                    x = 0f
                    y += lineHeight
                    lastWordBreak = -1
                    lastBreakOpportunity = -1
                }
            }

            if(glyph.metrics.isWhitespace) {
                lastWordBreak = list.size
            }
            if(glyph.metrics.canBreakBefore) {
                lastBreakOpportunity = list.size
            }
            if(glyph.metrics.canBreakAfter) {
                lastBreakOpportunity = list.size+1
            }

            list.add(GlyphDrawInfo(x, y, glyph, format, i))
            x += advance

            if(glyph.metrics.isLineBreak) {
                lastWordBreak = -1
                lastBreakOpportunity = -1
                x = 0f
                y += lineHeight
            }

            i++
        }

        return list
    }

    fun handleFormat(str: String, format_: TextFormatting, defaultFormat: TextFormatting): TextFormatting {
        if(str.length == 0) {
            LibrarianLog.warn("Somehow an empty format string got passed to GlyphLayout.handleFormat")
            return format_ // Why are we even here? I dunno, but I don't want to crash
        }
        var format = format_.clone()

        var singleChrs: String? = null
        if(str.length == 1) {
            singleChrs = str
        } else if(match(compoundExtract, str)) {
            singleChrs = lastMatchResult
        }

        if(singleChrs != null) {
            for(chr in singleChrs) {
                val s = Character.toString(chr)
                if(s in "kK") {
                    LibrarianLog.warn("Obfuscated text style isn't supported, and will never be. Who even uses it anyway?")
                } else

                if(s in "rR") {
                    format = defaultFormat.clone()
                } else

                if(s in "0123456789abcdefABCDEF") {
                    val i = Integer.parseInt(s, 16)

                    val bright = (i shr 3 and 1) * 85
                    var r = (i shr 2 and 1) * 170 + bright
                    val g = (i shr 1 and 1) * 170 + bright
                    val b = (i shr 0 and 1) * 170 + bright

                    if (i == 6) {
                        r += 85
                    }

                    format.text = Color(r and 255, g and 255, b and 255)
                } else
                if(s in "nN") {
                    format.underline = TextFormatting.DEFAULT
                } else
                if(s in "mM") {
                    format.strikethrough = TextFormatting.DEFAULT
                } else

                if(s in "oO") {
                    format.font = format.font.withStyle(format.font.getStyle() or Font.ITALIC)
                } else
                if(s in "lL") {
                    format.font = format.font.withStyle(format.font.getStyle() or Font.BOLD)
                }
            }
        }

        val color = parseColor(if(str.length < 3) "" else str.substring(1, str.length-1))
        if(color != null) {
            format.text = color
        }

        if(match(underlineRegex, str)) {
            format.underline = parseColor(lastMatchResult.toLowerCase())
        }
        if(match(strikeRegex, str)) {
            format.strikethrough = parseColor(lastMatchResult.toLowerCase())
        }
        if(match(shadowRegex, str)) {
            format.shadow = parseColor(lastMatchResult.toLowerCase())
        }
        if(match(styleRegex, str)) {
            lastMatchResult = lastMatchResult.toLowerCase()
            val i =
                    if(lastMatchResult == "plain")
                        Font.PLAIN
                    else if(lastMatchResult == "bold")
                        Font.BOLD
                    else if(lastMatchResult == "italic")
                        Font.ITALIC
                    else if(lastMatchResult == "bold_italic")
                        Font.BOLD or Font.ITALIC
                    else
                        Font.PLAIN
            format.font = format.font.withStyle(i)
        }
        if(match(fontRegex, str)) {
            val f = FontLoader.font(lastMatchResult, format.font.getStyle(), format.targetSize)
            format.font = f.first
            format.scale = f.second
        }

        return format
    }

    fun parseColor(str: String): Color? {
        if(str == "off")
            return null
        if(str == "on")
            return TextFormatting.DEFAULT

        val color = cssColors[str]
        var hex = if(str.startsWith("#")) str.substring(1) else str
        if(color != null)
            hex = color

        try {
            if(hex.length == 3)
                hex = "${hex[0]}${hex[0]}${hex[1]}${hex[1]}${hex[2]}${hex[2]}"
            else if(hex.length == 4)
                hex = "${hex[0]}${hex[0]}${hex[1]}${hex[1]}${hex[2]}${hex[2]}${hex[3]}${hex[3]}"
            return Color(Integer.parseUnsignedInt(hex, 16), hex.length == 8)
        } catch (e: NumberFormatException) {
            return null
        }
    }

    var lastMatchResult = ""
    fun match(regex: Regex, str: String): Boolean {
        val match = regex.matchEntire(str)
        if(match == null) {
            lastMatchResult = ""
            return false
        } else {
            lastMatchResult = match.groups[1]?.value.toString()
            return true
        }
    }

    fun validFormatLength(str: String, i: Int): Int {
        if(i >= str.length)
            return 0
        val c = str[i]
        if(c in validOneCharFormats) {
            return 1
        }
        val sub = str.substring(i)
        val result = formatRegex.find(sub) ?: compoundRegex.find(sub)
        if(result != null) {
            return result.value.length
        }

        return 0
    }

    companion object {
        val validOneCharFormats = "0123456789abcdefABCDEFlmnorLMNOR"
        val formatRegex = Regex("^\\{.*?\\}")
        val compoundRegex = Regex("^\\[.*?\\]")
        val compoundExtract = Regex("\\[(\\w+)\\]")

        val underlineRegex = formatRegex("underline")
        val strikeRegex = formatRegex("strike")

        val shadowRegex = formatRegex("shadow")
        val styleRegex = formatRegex("style")
        val fontRegex = formatRegex("font")

        private fun formatRegex(str: String) = Regex("\\{$str=(.+)\\}")

        val cssColors: Map<String, String> by lazy {
            val input = GlyphLayout::class.java.getResourceAsStream("/assets/librarianlib/font/colors.txt")
            val l = IOUtils.toString(input, Charsets.UTF_8).replace('\n', ',').split(',')

            val map = mutableMapOf<String, String>()
            for(i in 0..(l.size-1) step 2) {
                if(i+1 < l.size)
                    map[l[i]] = l[i+1]
            }
            return@lazy map
        }
    }

}

data class GlyphDrawInfo(var x: Float, var y: Float, val glyph: Glyph, val formatting: TextFormatting, val stringIndex: Int)

class TextFormatting protected constructor(var font: BasicFont, var targetSize: Int, var scale: Float, text: Color, shadow: Color? = null, underline: Color? = null, strikethrough: Color? = null) {
    constructor(fontName: String, style: Int, targetSize: Int) : this(FontLoader.font(fontName, style, targetSize).first, targetSize, FontLoader.font(fontName, style, targetSize).second, Color.BLACK)

    var text: Color = text
        set(value) {
            field = value
            darkerTextColor = Color(value.red/4, value.green/4, value.blue/4)
        }
    var darkerTextColor: Color = Color(text.red/4, text.green/4, text.blue/4)
        private set

    var shadow: Color? = shadow
        get() {
            if(field === DEFAULT) {
                return darkerTextColor
            }
            return field
        }

    var underline: Color? = underline
        get() {
            if(field === DEFAULT) {
                return text
            }
            return field
        }

    var strikethrough: Color? = strikethrough
        get() {
            if(field === DEFAULT) {
                return text
            }
            return field
        }

    fun clone(): TextFormatting {
        return TextFormatting(font, targetSize, scale, text, shadow, underline, strikethrough)
    }

    fun clone(fontName: String, style: Int, size: Int): TextFormatting {
        val f = FontLoader.font(fontName, style, size)
        return TextFormatting(f.first, targetSize, f.second, text, shadow, underline, strikethrough)
    }

    companion object {
        val DEFAULT = Color(0, 0, 0, 0)
    }
}
