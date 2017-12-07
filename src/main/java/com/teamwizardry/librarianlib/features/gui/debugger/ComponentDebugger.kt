package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.renderer.GlStateManager
import no.birkett.kiwi.Strength
import org.lwjgl.opengl.GL11.GL_ALWAYS
import org.lwjgl.opengl.GL11.GL_LEQUAL
import java.awt.Color

/**
 * Manages the debug panel
 */
class ComponentDebugger : GuiComponent(0, 0, 0, 0) {
    val bottomAligned = ComponentVoid(0, 0)
    val debugPanel = ComponentDebugPanel()
    /** Used to make the resize bar positioned relative to the bottom of the screen so dragging works good */
    val resizeBar = ComponentRect(0, 0, 0, 8)
    /** Flattens the depth buffer before drawing, allowing us to draw over the other GUI */
    val flatten = ComponentRect(0, 0, 0, 0)

    init {
        // move the flattened plane back a bit so we are above it
        flatten.transform.translateZ = -1.0

        add(bottomAligned)

        resizeBar.color = Color(0xA6A6A6)
        bottomAligned.add(debugPanel)
        bottomAligned.add(resizeBar)

        resizeBar.pos = vec(0, -debugPanel.size.y - resizeBar.size.y)
        resizeBar.layout.topStay = Strength.STRONG
        resizeBar.layout.heightStay = Strength.REQUIRED
        DragMixin(resizeBar) {
            vec(0, it.y.clamp(-size.y, -resizeBar.size.y))
        }

        layout {
            resizeBar.layout.left eq this.layout.left
            resizeBar.layout.right eq this.layout.right
            debugPanel.layout.left eq this.layout.left
            debugPanel.layout.right eq this.layout.right

            debugPanel.layout.bottom eq this.layout.bottom
            debugPanel.layout.top eq resizeBar.layout.bottom
        }

        this.layout.boundsStay = Strength.REQUIRED

        resizeBar.render.hoverCursor = LibCursor.RESIZE_UPDOWN
    }

    /**
     * Flatten the depth buffer before drawing so we will be able to draw over everything else
     */
    @Hook
    fun flattenDepth(e: GuiComponentEvents.PreDrawEvent) {
        bottomAligned.pos = vec(0, size.y)

        GlStateManager.depthFunc(GL_ALWAYS)
        GlStateManager.colorMask(false, false, false, false)
        flatten.size = this.size
        flatten.render.draw(Vec2d.ZERO, 0f)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthFunc(GL_LEQUAL)
    }

    companion object {
        val PANEL_RESIZE_HANDLE = DebuggerConst.TEXTURE.getSprite("panel_resize_handle", 16, 13)
    }
}
