package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.client.util.cache
import com.teamwizardry.librarianlib.client.util.putCache
import com.teamwizardry.librarianlib.client.vbo.VboCache
import com.teamwizardry.librarianlib.client.vbo.VertexBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class StringRenderer {
    companion object {
//        private val textBuffer = VertexBuffer(50000)
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
    var textColor = Color.BLACK
    var shadowColor = Color(0, 0, 0, 0)

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

    private var cache: VboCache? = null

    fun buildText() {

        val list = glyphLayout.layout(text, TextFormatting(textColor))

        buildGlyphBuffer(list)
        dirty = false
    }

    fun buildGlyphBuffer(list: List<GlyphDrawInfo>) {
        val w = FontLoader.bitmapFont.textureWidth
        val h = FontLoader.bitmapFont.textureHeight
        val f = BitmapFont.format
        f.start(VertexBuffer.INSTANCE)
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

            val tR = info.formatting.text.red/255f
            val tG = info.formatting.text.green/255f
            val tB = info.formatting.text.blue/255f
            val tA = info.formatting.text.alpha/255f

            val sR = if(info.formatting.shadow == null) 0f else info.formatting.shadow.red/255f
            val sG = if(info.formatting.shadow == null) 0f else info.formatting.shadow.green/255f
            val sB = if(info.formatting.shadow == null) 0f else info.formatting.shadow.blue/255f
            val sA = if(info.formatting.shadow == null) 0f else info.formatting.shadow.alpha/255f

            f.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
            f.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
            f.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
            f.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
        }
        cache = f.cache()
    }

    fun render(posX: Int, posY: Int) {

        if(dirty) {
            buildText()
        }

        val scale = 1.0/ screenScale

        GlStateManager.pushMatrix()
        GlStateManager.translate(posX.toDouble(), posY.toDouble(), 0.0)
        GlStateManager.scale(scale, scale, 1.0)
        GlStateManager.enableBlend()
        GlStateManager.color(0f, 0f, 0f, 1f)

        val f = BitmapFont.format

        val c = cache

        if(c != null) {
            FontLoader.bitmapFont.enableShader()
            GlStateManager.bindTexture(FontLoader.bitmapFont.textureID)

            f.start(VertexBuffer.INSTANCE)
            f.addCache(c)
            f.draw(GL11.GL_QUADS)

            FontLoader.bitmapFont.disableShader()
        }

        GlStateManager.popMatrix()
    }


}