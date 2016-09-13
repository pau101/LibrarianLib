package com.teamwizardry.librarianlib.client.font

import java.awt.Color
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class GlyphLayout(val font: BitmapFont) {

    fun layout(str: String): List<GlyphDrawInfo> {
        var x = 0f
        var y = 0f

        val list = ArrayList<GlyphDrawInfo>(str.length)

        for(c in str) {
            val glyph = font.getGlyph(c.toInt())
            list.add(GlyphDrawInfo(x, y, glyph, TextFormatting(Color.BLACK)))
            x += glyph.metrics.advance
        }

        return list
    }

}

data class GlyphDrawInfo(val x: Float, val y: Float, val glyph: Glyph, val formatting: TextFormatting)

data class TextFormatting(val text: Color, val shadow: Color? = null, val underline: Color? = null, val strikethrough: Color? = null)
