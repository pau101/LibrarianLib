package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.LibrarianLog
import java.awt.Color
import java.awt.Font
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class GlyphLayout() {

    fun layout(str: String, format_: TextFormatting): List<GlyphDrawInfo> {
        var format = format_

        var x = 0f
        var y = 0f

        val list = ArrayList<GlyphDrawInfo>(str.length)

        var i = 0
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
            list.add(GlyphDrawInfo(x, y, glyph, format))
            x += glyph.metrics.advance

            i++
        }

        return list
    }

    fun handleFormat(str: String, format_: TextFormatting, defaultFormat: TextFormatting): TextFormatting {
        if(str.length == 0) {
            LibrarianLog.warn("Somehow an empty format string got passed to GlyphLayout.handleFormat")
            return format_ // Why are we even here? I dunno, but I don't want to crash
        }
        var format = format_

        var singleChrs: String? = null
        if(str.length == 1) {
            singleChrs = str
        } else if(match(compoundExtract, str)) {
            singleChrs = lastMatchResult
        }

        if(singleChrs != null) {
            format = format_.clone()

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

//        if(match(underlineRegex, str)) {
//            val color =
//        }
        return format // TODO: Implement
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

        private fun formatRegex(str: String) = Regex("\\{$str=(.+)\\}")

    }

}

data class GlyphDrawInfo(val x: Float, val y: Float, val glyph: Glyph, val formatting: TextFormatting)

class TextFormatting(var font: BasicFont, text: Color, shadow: Color? = null, underline: Color? = null, strikethrough: Color? = null) {
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
        return TextFormatting(font, text, shadow, underline, strikethrough)
    }

    companion object {
        val DEFAULT = Color(0, 0, 0, 0)
    }
}
