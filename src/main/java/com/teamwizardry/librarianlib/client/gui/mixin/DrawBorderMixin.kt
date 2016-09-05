package com.teamwizardry.librarianlib.client.gui.mixin

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.Option
import com.teamwizardry.librarianlib.client.util.glColor
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class DrawBorderMixin<T : GuiComponent<T>>(private val component: T) {
    val color: Option<T, Color> = Option(Color.WHITE)

    init {
        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
            GlStateManager.pushAttrib()
            color.getValue(component).glColor()
            GlStateManager.disableTexture2D()
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            vb.pos(component.pos.x,                    component.pos.y,                    0.0).endVertex()
            vb.pos(component.pos.x + component.size.x, component.pos.y,                    0.0).endVertex()
            vb.pos(component.pos.x + component.size.x, component.pos.y + component.size.y, 0.0).endVertex()
            vb.pos(component.pos.x,                    component.pos.y + component.size.y, 0.0).endVertex()
            vb.pos(component.pos.x,                    component.pos.y,                    0.0).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.popAttrib()
        }
    }
}