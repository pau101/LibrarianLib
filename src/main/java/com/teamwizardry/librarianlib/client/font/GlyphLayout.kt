package com.teamwizardry.librarianlib.client.font

import java.awt.Color
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class GlyphLayout(val font: BitmapFont) {

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
                    i += len+1

                    format = handleFormat(formatStr, format)
                }
            }
            
            val glyph = font.getGlyph(c.toInt())
            list.add(GlyphDrawInfo(x, y, glyph, format))
            x += glyph.metrics.advance

            i++
        }

        return list
    }

    fun handleFormat(str: String, format: TextFormatting): TextFormatting {
        return format // TODO: Implement
    }

    fun validFormatLength(str: String, i: Int): Int {
        if(i >= str.length)
            return 0
        val c = str[i]
        if(c in validOneCharFormats) {
            return 1
        }
        val result = formatRegex.find(str.substring(i))
        if(result != null) {
            return result.value.length
        }
        return 0
    }

    companion object {
        val validOneCharFormats = "0123456789abcdefABCDEFlmnorLMNOR"
        val formatRegex = Regex.fromLiteral("^\\{.*\\}")
    }

}

data class GlyphDrawInfo(val x: Float, val y: Float, val glyph: Glyph, val formatting: TextFormatting)

class TextFormatting(val text: Color, val shadow: Color? = null, val underline: Color? = null, val strikethrough: Color? = null)
