package com.teamwizardry.librarianlib.client.font

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.utils.Align
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.core.GLTextureExport
import com.teamwizardry.librarianlib.client.util.ScissorUtil
import com.teamwizardry.librarianlib.client.util.cache
import com.teamwizardry.librarianlib.client.util.putCache
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GLContext

/**
 * Created by TheCodeWarrior
 */
class StringRenderer {
    companion object {
        private val textBuffer = VertexBuffer(50000)
        private val layout = GlyphLayout()
        private var screenScale = 1
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun updateResolution(event: GuiScreenEvent.InitGuiEvent.Pre?) {
            screenScale = ScaledResolution(Minecraft.getMinecraft()).scaleFactor
        }
    }

    private var screenScaleUsed = screenScale
        set(value) {
            if(value != field) {
                dirty = true
                updateFont()
            }
            field = value
        }
    var pointSize = 8
        set(value) {
            if(value != field) {
                dirty = true
                updateFont()
            }
            field = value
        }

    var wrap = 0
    var fontFamily = EnumFont.HELVETICA
        set(value) {
            if(value != field) {
                dirty = true
                updateFont()
            }
            field = value
        }

    private var font: BitmapFont? = null
    private var fontScale: Float = 1f

    private fun updateFont() {
        val (font, scale) = FontLoader.getFont(fontFamily, EnumFontStyle.NORMAL, pointSize*screenScaleUsed)
        this.font = font
        this.fontScale = scale
    }

    private var text = ""
    private var dirty = false

    fun addText(str: String) {
        text += str
        dirty = true
    }
    fun setText(str: String) {
        text = str
        dirty = true
    }

    private val bufferByPage = mutableMapOf<Int, IntArray>()
    private val glyphByPage = mutableMapOf<Int, MutableList<CompiledGlyph>>()

    fun buildText() {
        updateFont()

        val font = this.font
        if(font == null) {
            LibrarianLog.warn("[StringRenderer] Couldn't find font $fontFamily! Text build canceled!")
            return
        }

        try {
            GLContext.getCapabilities()
        } catch(e: RuntimeException) {
            LibrarianLog.warn("[StringRenderer] Tried to build text in a non-opengl context! This can cause glitches when "+
                    "new glyphs have to be added to the map!")
        }

        bufferByPage.clear()
        if(wrap < 1) {
            layout.setText(font, text)
        } else {
            layout.setText(font, text, Color.BLACK, wrap*4f, Align.left, true)
        }
        var y = 0f
        for(run in layout.runs) {
            buildRunGlyphs(run, y)
            y += font.lineHeight
        }

        buildGlyphBuffers(font)

        glyphByPage.clear()

        dirty = false
    }

    fun buildRunGlyphs(run: GlyphLayout.GlyphRun, y: Float) {
        var x = run.xAdvances[0]

        for((index, glyph) in run.glyphs.withIndex()) {
            glyphByPage.getOrPut(glyph.page, { mutableListOf() }).add(CompiledGlyph(x, y, glyph))
            x += run.xAdvances[index+1]
        }
    }

    fun buildGlyphBuffers(f: BitmapFont) {
        for((page, glyphs) in glyphByPage.entries) {

            textBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

            val region = f.getRegion(page)
            val texture = region.texture

            for(glyph in glyphs) {

                if(glyph.glyph.width == 0 || glyph.glyph.height == 0)
                    continue

                val minX: Float = glyph.x + glyph.glyph.xoffset
                val minY: Float = glyph.y + glyph.glyph.yoffset
                val maxX: Float = glyph.x + glyph.glyph.xoffset + glyph.glyph.width
                val maxY: Float = glyph.y + glyph.glyph.yoffset + glyph.glyph.height

                val minU: Float = (glyph.glyph.srcX) / region.regionWidth .toFloat()
                val minV: Float = (glyph.glyph.srcY) / region.regionHeight.toFloat()
                val maxU: Float = (glyph.glyph.srcX + glyph.glyph.width ) / region.regionWidth .toFloat()
                val maxV: Float = (glyph.glyph.srcY + glyph.glyph.height) / region.regionHeight.toFloat()

                textBuffer.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
                textBuffer.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
                textBuffer.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
                textBuffer.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()
            }

            bufferByPage[page] = textBuffer.cache()
        }
    }

    fun render(posX: Int, posY: Int) {
        if(dirty) {
            buildText()
        }
        val font = font
        if(font == null) {
            LibrarianLog.warn("[StringRenderer] Couldn't find font $fontFamily! Text render canceled!")
            return
        }

        screenScaleUsed = screenScale
        val scale = 1.0/( fontScale * screenScaleUsed )

        GlStateManager.pushMatrix()
        GlStateManager.translate(posX.toDouble(), posY.toDouble(), 0.0)
        GlStateManager.scale(scale, scale, 1.0)
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.color(1f, 1f, 1f, 1f)

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        for((page, buffer) in bufferByPage.entries) {
            font.regions[page].texture.bind()
//            GLTextureExport.saveGlTexture("font_$page", 1)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            vb.putCache(buffer)
            tessellator.draw()
        }

        GlStateManager.popMatrix()
    }


}

data class CompiledGlyph(val x: Float, val y: Float, val glyph: BitmapFont.Glyph)