package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.supporting.ComponentEventHookMethodHandler
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.debugger.ComponentDebugger
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketSyncSlotVisibility
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import no.birkett.kiwi.Solver
import no.birkett.kiwi.Strength
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException

open class GuiContainerBase(val container: ContainerBase, val guiWidth: Int, val guiHeight: Int) : GuiContainer(container) {
    @Suppress("LeakingThis")
    private val baseGuiImplementation = BaseGuiImplementation(this, guiWidth, guiHeight, { false /* we can't scale the slots, so we can't scale the GUI */ })
    val mainComponents = baseGuiImplementation.mainComponents
    val fullscreenComponents = baseGuiImplementation.fullscreenComponents


    fun layout(runnable: Runnable) = baseGuiImplementation.layout(runnable)
    fun layout(lambda: () -> Unit) = baseGuiImplementation.layout(Runnable(lambda))

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {}

    override fun initGui() {
        super.initGui()
        baseGuiImplementation.scaleGui()
    }

    override fun drawDefaultBackground() { /* NOOP */
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        container.slots.forEach { it.lastVisible = it.visible; it.visible = false }

        baseGuiImplementation.drawScreen(mouseX, mouseY, partialTicks)

        if (container.slots.any { it.lastVisible != it.visible }) {
            PacketHandler.NETWORK.sendToServer(PacketSyncSlotVisibility(container.slots.map { it.visible }.toBooleanArray()))
        }

        container.slots.forEach {
            if(it.visible) {
                it.xPos -= this.guiLeft
                it.yPos -= this.guiTop
            } else {
                it.xPos = -1000
                it.yPos = -1000
            }
        }

        GlStateManager.pushMatrix()
        baseGuiImplementation.debugTransform()
        super.drawScreen(mouseX, mouseY, partialTicks)
        GlStateManager.popMatrix()
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
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun tick(e: TickEvent.ClientTickEvent) {
            val gui = Minecraft.getMinecraft().currentScreen
            if (gui is GuiContainerBase) {
                gui.baseGuiImplementation.tick()
            }
        }
    }
}
