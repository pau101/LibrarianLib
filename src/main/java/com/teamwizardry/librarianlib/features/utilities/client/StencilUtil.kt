package com.teamwizardry.librarianlib.features.utilities.client

import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11.*

/**
 * TODO: Document file StencilUtil
 *
 * Created by TheCodeWarrior
 */
object StencilUtil {
    var currentStencil = 0
        private set

    fun clear() {
        glEnable(GL_STENCIL_TEST)
        glStencilMask(0xFF)
        glClearStencil(0)
        glClear(GL_STENCIL_BUFFER_BIT)
        glDisable(GL_STENCIL_TEST)
        currentStencil = 0
    }

    @JvmStatic
    fun push(draw: Runnable) {
        currentStencil += 1

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_NEVER, 0, 0xFF)
        glStencilOp(GL_INCR, GL_KEEP, GL_KEEP)

        glStencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun pop(draw: Runnable) {
        currentStencil -= 1

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_NEVER, 0, 0xFF)
        glStencilOp(GL_DECR, GL_KEEP, GL_KEEP)

        glStencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    fun push(kotlin: () -> Unit) = push(Runnable(kotlin))
    fun pop(kotlin: () -> Unit) = pop(Runnable(kotlin))
}
