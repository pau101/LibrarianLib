package com.teamwizardry.librarianlib.client.font

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.core.GLTextureExport
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
import org.newdawn.slick.TrueTypeFont
import scala.annotation.meta.field

/**
 * Created by TheCodeWarrior
 */
class StringRenderer {
    companion object {
        private val textBuffer = VertexBuffer(50000)
        private var screenScale = 1

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun updateResolution(event: GuiScreenEvent.InitGuiEvent.Pre?) {
            screenScale = ScaledResolution(Minecraft.getMinecraft()).scaleFactor
        }
    }

    private var glyphLayout = GlyphLayout(FontLoader.bitmapFont)

    var wrap = 0

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

    private var buffer: IntArray? = null

    fun buildText() {

        val list = glyphLayout.layout(text)

        buildGlyphBuffer(list)
        dirty = false
    }

    fun buildGlyphBuffer(list: List<GlyphDrawInfo>) {
        val w = FontLoader.bitmapFont.textureWidth
        val h = FontLoader.bitmapFont.textureHeight
        textBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        for(info in list) {

            val glyph = info.glyph

            if(glyph.metrics.width == 0 || glyph.metrics.height == 0)
                continue

            val minX: Float = info.x + glyph.metrics.bearingX
            val minY: Float = info.y - glyph.metrics.bearingY
            val maxX: Float = minX + glyph.metrics.width //+ glyph.metrics.bearingX
            val maxY: Float = minY + glyph.metrics.height//+ glyph.metrics.bearingY

            val minU: Float = (glyph.u ).toFloat() / w
            val minV: Float = (glyph.v ).toFloat() / h
            val maxU: Float = (glyph.u + glyph.width ).toFloat() / w
            val maxV: Float = (glyph.v + glyph.height ).toFloat() / h

            textBuffer.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
            textBuffer.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
            textBuffer.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
            textBuffer.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()

        }
        buffer = textBuffer.cache()
    }

    fun render(posX: Int, posY: Int) {

        if(dirty) {
            buildText()
        }

        val scale = 1.0/ screenScale

        GlStateManager.pushMatrix()
        GlStateManager.translate(posX.toDouble(), posY.toDouble(), 0.0)
        GlStateManager.scale(scale, scale, 1.0)
//        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.color(0f, 0f, 0f, 1f)

        val buf = buffer

        if(buf != null) {
            GlStateManager.bindTexture(FontLoader.bitmapFont.textureID)
//            GLTextureExport.saveGlTexture("font", 1)
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer

            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            vb.putCache(buf)
            tessellator.draw()
        }
        GlStateManager.popMatrix()
    }


}