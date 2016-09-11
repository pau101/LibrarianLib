package com.teamwizardry.librarianlib.client.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandleStream
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.teamwizardry.librarianlib.LibrarianLib
import java.io.InputStream

/**
 * Created by TheCodeWarrior
 */
object LLFontRenderer {

    val standard: BitmapFont

    init {

        standard = loadFont("standard")

    }

    private fun loadFont(style: String): BitmapFont {
        val stream = LibrarianLib::class.java.getResourceAsStream("/assets/librarianlib/font/helvetica/$style.ttf")

        val generator = FreeTypeFontGenerator(FileHandleInputStream(stream))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 32
        parameter.color = Color.RED
        parameter.shadowColor = Color.GREEN
        parameter.shadowOffsetX = 2
        parameter.shadowOffsetY = 2
        parameter.flip = true
        parameter.incremental = true
        parameter.genMipMaps = true
//        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=_+`~[]{}\\|;:'\",<.>/?"

        val font = generator.generateFont(parameter)
//        generator.dispose()
        return font
    }




    fun render(str: String, x: Int, y: Int) {
//        val cache = standard.cache
        val regions = standard.regions



//        cache.clear()
//        val layout = cache.addText(str, x.toFloat(), y.toFloat())
//
//        val pageVertices = LLFontRendererMethodHandles.get_BitmapFontCache_pageVertices(cache)
//        val idx = LLFontRendererMethodHandles.get_BitmapFontCache_idx(cache)
//
//        var j = 0
//        val n = pageVertices.size
//        while (j < n) {
//            if (idx[j] > 0) { // ignore if this texture has no glyphs
//                val vertices = pageVertices[j]
//                spriteBatch.draw(regions.get(j).getTexture(), vertices, 0, idx[j])
//            }
//            j++
//        }
    }

}

private class FileHandleInputStream(val stream: InputStream) : FileHandleStream("foobar") {
    override fun read() = stream
}