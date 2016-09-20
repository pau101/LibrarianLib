package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.client.core.GLTextureExport
import com.teamwizardry.librarianlib.client.util.cache
import com.teamwizardry.librarianlib.client.util.putCache
import com.teamwizardry.librarianlib.client.vbo.PosColorFormat
import com.teamwizardry.librarianlib.client.vbo.VboCache
import com.teamwizardry.librarianlib.client.vbo.VertexBuffer
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import scala.annotation.meta.field
import sun.security.provider.certpath.Vertex
import java.awt.Color
import java.awt.Font

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

    private var glyphLayout = GlyphLayout()
    private var cachedScreenScale = -1


    private val formatting = TextFormatting("Unifont", Font.PLAIN, 16)
    private var fontStyle = Font.PLAIN
    var textColor: Color
        set(value) { formatting.text = value; dirty = true }
        get() = formatting.text
    var shadowColor: Color?
        set(value) { formatting.shadow = value; dirty = true }
        get() = formatting.shadow
    var strikeColor: Color?
        set(value) { formatting.strikethrough = value; dirty = true }
        get() = formatting.strikethrough
    var underlineColor: Color?
        set(value) { formatting.underline = value; dirty = true }
        get() = formatting.underline

    var bold: Boolean
        set(value) { fontStyle = if(value) fontStyle or Font.BOLD else fontStyle and Font.BOLD.inv(); dirty = true}
        get() = (fontStyle and Font.BOLD) != 0
    var italic: Boolean
        set(value) { fontStyle = if(value) fontStyle or Font.ITALIC else fontStyle and Font.ITALIC.inv(); dirty = true}
        get() = (fontStyle and Font.ITALIC) != 0

    var fontName: String = "Unifont"
        set(value) { field = value; dirty = true }
    var fontSize: Int
        set(value) { formatting.targetSize = value; dirty = true }
        get() = formatting.targetSize

    var wrap = 0
        set(value) { field = value; dirty = true }
    var text = ""
        set(value) { field = value; dirty = true }
    private var dirty = false

    fun addText(str: String) {
        text += str
    }

    private var caches = mutableMapOf<Texture, VboCache>()
    private var underlineCache: VboCache? = null
    private var strikeCache: VboCache? = null

    fun buildText() {
        cachedScreenScale = screenScale

        glyphLayout.wrap = wrap
        glyphLayout.text = text
        glyphLayout.format = formatting.clone()
        glyphLayout.fontSize = fontSize * screenScale
        glyphLayout.fontName = fontName
        glyphLayout.fontStyle = fontStyle

        val list = glyphLayout.layoutGlyphs()

        buildGlyphBuffer(list)
        dirty = false
    }

    fun buildGlyphBuffer(list: List<GlyphDrawInfo>) {
        caches.clear()

        val map = mutableMapOf<Texture, MutableList<GlyphDrawInfo>>()
        for(info in list) {
            map.getOrPut(info.glyph.texture, { mutableListOf() }).add(info)
        }

        val f = FontRenderer.format

        for( (tex, glyphs) in map.entries ) {
            f.start(VertexBuffer.INSTANCE)
            for(info in glyphs) {

                val glyph = info.glyph

                if(glyph.metrics.width == 0 || glyph.metrics.height == 0)
                    continue

                val minX: Float = info.x + ( glyph.metrics.bearingX * info.formatting.scale ).toInt()
                val minY: Float = info.y - ( glyph.metrics.bearingY * info.formatting.scale ).toInt()
                val maxX: Float = minX + ( glyph.metrics.width * info.formatting.scale ).toInt()
                val maxY: Float = minY + ( glyph.metrics.height * info.formatting.scale ).toInt()

                val shearTop: Float = ( glyph.metrics.bearingY * info.formatting.font.getShear() ) * info.formatting.scale
                val shearBottom: Float = ( (glyph.metrics.bearingY - glyph.metrics.height) * info.formatting.font.getShear() ) * info.formatting.scale

                val minU: Float = (glyph.u ).toFloat()
                val minV: Float = (glyph.v ).toFloat()
                val maxU: Float = (glyph.u + glyph.width ).toFloat()
                val maxV: Float = (glyph.v + glyph.height ).toFloat()

                val tR = info.formatting.text.red/255f
                val tG = info.formatting.text.green/255f
                val tB = info.formatting.text.blue/255f
                val tA = info.formatting.text.alpha/255f

                val sR = info.formatting.shadow.let { if(it == null) 0f else it.red  /255f }
                val sG = info.formatting.shadow.let { if(it == null) 0f else it.green/255f }
                val sB = info.formatting.shadow.let { if(it == null) 0f else it.blue /255f }
                val sA = info.formatting.shadow.let { if(it == null) 0f else it.alpha/255f }

                f.pos(minX.toDouble()+shearTop,    minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
                f.pos(minX.toDouble()+shearBottom, maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
                f.pos(maxX.toDouble()+shearBottom, maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
                f.pos(maxX.toDouble()+shearTop,    minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).color(tR, tG, tB, tA).shadow(sR, sG, sB, sA).endVertex()
            }
            caches.put(tex, f.cache())
        }

        // =============================================================================================================

        val underlineDist = 2

        var underlinePos = Vec2d.ZERO
        var underlineColor: Color? = null

        var prevEnd = Vec2d.ZERO

        PosColorFormat.start(VertexBuffer.INSTANCE)
        for(info in list) {

            if(underlineColor != info.formatting.underline || info.y != underlinePos.yf) {
                if(underlineColor != null) {
                    PosColorFormat.pos(underlinePos.x, underlinePos.y+underlineDist, 0).color(underlineColor.red/255f, underlineColor.green/255f, underlineColor.blue/255f, 1).endVertex()
                    PosColorFormat.pos(prevEnd.x, prevEnd.y+underlineDist, 0)          .color(underlineColor.red/255f, underlineColor.green/255f, underlineColor.blue/255f, 1).endVertex()
                }
                underlineColor = info.formatting.underline
                underlinePos = Vec2d(info.x, info.y)
            }

            prevEnd = Vec2d(info.x + if(info.glyph.metrics.isWhitespace) 0f else info.glyph.metrics.advance*info.formatting.scale, info.y)
        }

        underlineCache = PosColorFormat.cache()

        // =============================================================================================================

        val strikeHeight = 5

        var strikePos = Vec2d.ZERO
        var strikeColor: Color? = null
        prevEnd = Vec2d.ZERO

        PosColorFormat.start(VertexBuffer.INSTANCE)
        for(info in list) {

//            newLine = newLine || info.y != strikePos.yf

            if(strikeColor != info.formatting.strikethrough || info.y != strikePos.yf) {
                if(strikeColor != null) {
                    PosColorFormat.pos(strikePos.x, strikePos.y-strikeHeight, 0).color(strikeColor.red/255f, strikeColor.green/255f, strikeColor.blue/255f, 1).endVertex()
                    PosColorFormat.pos(prevEnd.x, prevEnd.y-strikeHeight, 0)    .color(strikeColor.red/255f, strikeColor.green/255f, strikeColor.blue/255f, 1).endVertex()
                }
                strikeColor = info.formatting.strikethrough
                strikePos = Vec2d(info.x, info.y)
            }

            prevEnd = Vec2d(info.x + if(info.glyph.metrics.isWhitespace) 0f else info.glyph.metrics.advance*info.formatting.scale, info.y)
        }

        strikeCache = PosColorFormat.cache()
    }

    fun render(posX: Int, posY: Int) {

        if(dirty || cachedScreenScale != screenScale) {
            buildText()
        }

        GlStateManager.pushMatrix()
        GlStateManager.translate(posX.toDouble(), posY.toDouble(), 0.0)
        GlStateManager.scale(1.0/ screenScale, 1.0/ screenScale, 1.0)
        GlStateManager.enableBlend()
        GlStateManager.color(1f, 1f, 1f, 1f)

        val f = FontRenderer.format

        val c2 = underlineCache
        val c3 = strikeCache

        if(c2 != null && c3 != null) {
            GlStateManager.disableTexture2D()

            GlStateManager.glLineWidth(screenScale.toFloat())
            PosColorFormat.start(VertexBuffer.INSTANCE)
            PosColorFormat.addCache(c2)
            PosColorFormat.draw(GL11.GL_LINES)

            GlStateManager.enableTexture2D()

            // =============================================================================================================

            FontRenderer.enableShader()
            for( (tex, cache) in caches.entries) {
                GlStateManager.bindTexture(tex.textureID)
                FontRenderer.setShaderTexSize(tex)
                f.start(VertexBuffer.INSTANCE)
                f.addCache(cache)
                f.draw(GL11.GL_QUADS)
            }
            FontRenderer.disableShader()

            // =============================================================================================================

            GlStateManager.disableTexture2D()

            GlStateManager.glLineWidth(2f)
            PosColorFormat.start(VertexBuffer.INSTANCE)
            PosColorFormat.addCache(c3)
            PosColorFormat.draw(GL11.GL_LINES)

            GlStateManager.enableTexture2D()
        }

        GlStateManager.popMatrix()
    }


}