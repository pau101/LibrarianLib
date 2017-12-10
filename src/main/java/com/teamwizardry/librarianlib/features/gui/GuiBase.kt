package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.supporting.ComponentEventHookMethodHandler
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.debugger.ComponentDebugger
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import no.birkett.kiwi.Solver
import no.birkett.kiwi.Strength
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.io.IOException

open class GuiBase(val guiWidth: Int, val guiHeight: Int) : GuiScreen() {
    @Suppress("LeakingThis")
    private val baseGuiImplementation = BaseGuiImplementation(this, guiWidth, guiHeight, { shouldAutoScale })
    val mainComponents = baseGuiImplementation.mainComponents
    val fullscreenComponents = baseGuiImplementation.fullscreenComponents

    /**
     * Set to false to disable shrinking the GUI when it is larger than the viewport
     */
    var shouldAutoScale = false

    fun layout(runnable: Runnable) = baseGuiImplementation.layout(runnable)
    fun layout(lambda: () -> Unit) = baseGuiImplementation.layout(Runnable(lambda))

    override fun initGui() {
        super.initGui()
        baseGuiImplementation.scaleGui()
    }

    override fun drawDefaultBackground() { /* NOOP */ }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        baseGuiImplementation.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        baseGuiImplementation.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        baseGuiImplementation.mouseReleased(mouseX, mouseY, state)
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        baseGuiImplementation.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    override fun handleKeyboardInput() {
        super.handleKeyboardInput()
        baseGuiImplementation.handleKeyboardInput()
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        baseGuiImplementation.handleMouseInput()
    }

    companion object {
        init { MinecraftForge.EVENT_BUS.register(this) }

        @SubscribeEvent
        fun tick(e: TickEvent.ClientTickEvent) {
            val gui = Minecraft.getMinecraft().currentScreen
            if (gui is GuiBase) {
                gui.baseGuiImplementation.tick()
            }
        }
    }
}
