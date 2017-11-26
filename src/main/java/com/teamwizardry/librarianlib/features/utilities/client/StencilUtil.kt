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

    fun start() {
        glEnable(GL_STENCIL_TEST)
        glStencilMask(0xFF)
        glClearStencil(0)
        glClear(GL_STENCIL_BUFFER_BIT)
        currentStencil = 0
    }

    fun end() {
        glDisable(GL_STENCIL_TEST)
    }

    @JvmStatic
    fun push(draw: Runnable) {

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_NEVER, 0, 0xFF)
        glStencilOp(GL_INCR, GL_KEEP, GL_KEEP)

        glStencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        currentStencil += 1

        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    @JvmStatic
    fun pop(draw: Runnable) {

        GlStateManager.depthMask(false)
        GlStateManager.colorMask(false, false, false, false)

        glStencilFunc(GL_NEVER, 0, 0xFF)
        glStencilOp(GL_DECR, GL_KEEP, GL_KEEP)

        glStencilMask(0xFF)
        draw.run()

        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthMask(true)

        currentStencil -= 1

        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, currentStencil, 0xFF)
    }

    fun push(kotlin: () -> Unit) = push(Runnable(kotlin))
    fun pop(kotlin: () -> Unit) = pop(Runnable(kotlin))
}
