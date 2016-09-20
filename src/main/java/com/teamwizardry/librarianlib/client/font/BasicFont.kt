package com.teamwizardry.librarianlib.client.font

/**
 * Created by TheCodeWarrior
 */
abstract class BasicFont {

    abstract fun getShear(): Float

    abstract fun getStyle(): Int
    abstract fun withStyle(style: Int): BasicFont

    abstract fun getGlyph(c: Int): Glyph

    fun getGlyphMetrics(c: Int): GlyphMetrics {
        return getGlyph(c).metrics
    }
}

data class Glyph(val texture: Texture, val c: Char, val u: Int, val v: Int, val width: Int, val height: Int, val metrics: GlyphMetrics)

data class GlyphMetrics(val font: BasicFont, val c: Char, val bearingX: Int, val bearingY: Int, val width: Int, val height: Int, val advance: Int, val isWhitespace: Boolean, val canBreakBefore: Boolean, val canBreakAfter: Boolean, val isLineBreak: Boolean)

abstract class Texture {
    abstract val textureID: Int
    abstract val width: Int
    abstract val height: Int
}