package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.core.GLTextureExport
import com.teamwizardry.librarianlib.client.fx.shader.ShaderProgram
import com.teamwizardry.librarianlib.client.vbo.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import sun.font.Font2D
import java.awt.*
import java.awt.image.BufferedImage

/**
 * Created by TheCodeWarrior
 */
class BitmapFont(val spec: FontSpecification, val font: Font, val antiAlias: Boolean, val shadowDistX: Int, val shadowDistY: Int) : PackedFont() {

    private val metrics: FontMetrics
    private val font2d: Font2D

    init {
        font2d = FontMethodHandles.call_Font_getFont2D(font)

        g.font = font
        if (antiAlias == true) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON)
        }

        metrics = g.fontMetrics

        postInit()
    }

    // =================================================================================================================
    override fun getShear(): Float = 0f

    override fun getStyle() = spec.style
    override fun withStyle(style: Int): BasicFont {
        return FontLoader.font(FontSpecification(spec.font, style, spec.resolution)) ?: this
    }

    // =================================================================================================================

    override fun getAdvance(c: Int) =  metrics.charWidth(c)

    override fun isWhitespace(c: Int) = font.createGlyphVector(g.fontRenderContext, asStr(c)).getGlyphMetrics(0).isWhitespace

    override fun drawToGraphcs(c: Int, g: Graphics2D, image: BufferedImage) {
        val str = asStr(c)

        g.color = Color.GREEN
        g.drawString(str, shadowDistX, shadowDistY)
        g.color = Color.RED
        g.drawString(str, 0, 0)
    }

    override fun fontMapExportLoc() = "font_${spec.font}_${if(spec.style == Font.PLAIN) "plain" else if(spec.style == Font.ITALIC) "italic" else if(spec.style == Font.BOLD) "bold" else if(spec.style == Font.BOLD or Font.ITALIC) "bold_italic" else "unknown" }_${spec.resolution}"
}