package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.supporting.ComponentEventHookMethodHandler
import com.teamwizardry.librarianlib.features.gui.component.supporting.ModifierKey
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.debugger.ComponentDebugger
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import no.birkett.kiwi.Solver
import no.birkett.kiwi.Strength
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException

internal class BaseGuiImplementation(
        private val gui: GuiScreen,
        private val guiWidth: Int, private val guiHeight: Int,
        private val autoScaleGetter: () -> Boolean
) {
    val mainComponents: ComponentVoid = ComponentVoid(0, 0)
    val fullscreenComponents: ComponentVoid = ComponentVoid(0, 0)

    private val shouldAutoScale: Boolean
        get() = autoScaleGetter()
    private val mainScaleWrapper: ComponentVoid = ComponentVoid(0, 0)
    private var isDebugMode = false
    private val debugger = ComponentDebugger()

    private val eventHookHandler = ComponentEventHookMethodHandler(gui, fullscreenComponents)

    init {
        fullscreenComponents.layout.rootSolver = Solver()
        debugger.layout.rootSolver = Solver()

        mainComponents.geometry.shouldCalculateOwnHover = false
        fullscreenComponents.geometry.shouldCalculateOwnHover = false
        mainScaleWrapper.zIndex = -100000 // really far back
        fullscreenComponents.add(mainScaleWrapper)
        mainScaleWrapper.add(mainComponents)

        mainComponents.size = vec(guiWidth, guiHeight)
        debugger.geometry.shouldCalculateOwnHover = false

        mainComponents.layout.boundsStay = Strength.REQUIRED
        mainScaleWrapper.layout.boundsStay = Strength.REQUIRED
        fullscreenComponents.layout.boundsStay = Strength.REQUIRED
    }

    fun scaleGui() {
        var s = 1.0
        if (shouldAutoScale) {
            var i = 1
            // find required scale, either 1x, 1/2x 1/3x, or 1/4x
            while ((guiWidth * s > gui.width || guiHeight * s > gui.height) && i < 4) {
                i++
                s = 1.0 / i
            }
        }

        val left = (gui.width / 2 - guiWidth * s / 2).toInt()
        val top = (gui.height / 2 - guiHeight * s / 2).toInt()

        if (mainScaleWrapper.pos.xi != left || mainScaleWrapper.pos.yi != top) {
            mainScaleWrapper.pos = vec(left, top)
            mainScaleWrapper.transform.scale = s
            mainScaleWrapper.size = vec(guiWidth * s, guiHeight * s)
        }

        fullscreenComponents.size = vec(gui.width, gui.height)


        val scaledresolution = ScaledResolution(gui.mc)

        debugger.transform.scale = 1.0/scaledresolution.scaleFactor
        debugger.size = vec(gui.width * scaledresolution.scaleFactor, gui.height * scaledresolution.scaleFactor)
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.enableBlend()
        val relPos = vec(mouseX, mouseY)
        GlStateManager.pushMatrix()

        if(isDebugMode) {
            GlStateManager.translate(gui.width / 2.0, gui.height / 2.0, 0.0)
            GlStateManager.rotate(-20f, 1f, 0f, 0f)
            GlStateManager.rotate(-20f, 0f, 1f, 0f)
            GlStateManager.translate(-gui.width / 2.0, -gui.height / 2.0, 0.0)
        }

        StencilUtil.start()
        fullscreenComponents.guiEventHandler.preLayout(relPos, partialTicks)
        fullscreenComponents.layout.updateAllLayoutsIfNeeded()

        fullscreenComponents.geometry.calculateMouseOver(relPos)
        fullscreenComponents.render.draw(relPos, partialTicks)
        fullscreenComponents.render.drawLate(relPos, partialTicks)

        GlStateManager.popMatrix()

        if(isDebugMode) {
            debugger.guiEventHandler.preLayout(relPos, partialTicks)
            debugger.layout.updateAllLayoutsIfNeeded()

            debugger.geometry.calculateMouseOver(relPos)
            debugger.render.draw(relPos, partialTicks)
            debugger.render.drawLate(relPos, partialTicks)
        }

        StencilUtil.end()
        Mouse.setNativeCursor((debugger.render.cursor ?: fullscreenComponents.render.cursor)?.lwjglCursor)
        debugger.render.cursor = null
        fullscreenComponents.render.cursor = null
    }

    @Throws(IOException::class)
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if(isDebugMode) debugger.guiEventHandler.mouseDown(vec(mouseX, mouseY), EnumMouseButton.getFromCode(mouseButton))
        if(!isDebugMode || !debugger.mouseOver)
            fullscreenComponents.guiEventHandler.mouseDown(vec(mouseX, mouseY), EnumMouseButton.getFromCode(mouseButton))
    }

    fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        if(isDebugMode) debugger.guiEventHandler.mouseUp(vec(mouseX, mouseY), EnumMouseButton.getFromCode(state))
        if(!isDebugMode || !debugger.mouseOver)
            fullscreenComponents.guiEventHandler.mouseUp(vec(mouseX, mouseY), EnumMouseButton.getFromCode(state))
    }

    fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        if(isDebugMode) debugger.guiEventHandler.mouseDrag(vec(mouseX, mouseY), EnumMouseButton.getFromCode(clickedMouseButton))
        if(!isDebugMode || !debugger.mouseOver)
            fullscreenComponents.guiEventHandler.mouseDrag(vec(mouseX, mouseY), EnumMouseButton.getFromCode(clickedMouseButton))
    }

    @Throws(IOException::class)
    fun handleKeyboardInput() {
        if(Keyboard.getEventKeyState()) {
            if(Keyboard.getEventKey() == Keyboard.KEY_D && ModifierKey.doModifiersMatch(ModifierKey.SHIFT, ModifierKey.CTRL)) {
                isDebugMode = !isDebugMode
            }

            if(Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                Minecraft.getMinecraft().renderManager.isDebugBoundingBox = !Minecraft.getMinecraft().renderManager.isDebugBoundingBox
            }
        }

        if (Keyboard.getEventKeyState()) {
            if(isDebugMode) debugger.guiEventHandler.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
            if(!isDebugMode || !debugger.mouseOver)
                fullscreenComponents.guiEventHandler.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else {
            if(isDebugMode) debugger.guiEventHandler.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
            if(!isDebugMode || !debugger.mouseOver)
                fullscreenComponents.guiEventHandler.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        }
    }

    @Throws(IOException::class)
    fun handleMouseInput() {
        val mouseX = Mouse.getEventX() * gui.width / gui.mc.displayWidth
        val mouseY = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1
        val wheelAmount = Mouse.getEventDWheel()

        if (wheelAmount != 0) {
            if(isDebugMode) debugger.guiEventHandler.mouseWheel(vec(mouseX, mouseY), GuiComponentEvents.MouseWheelDirection.fromSign(wheelAmount))
            if(!isDebugMode || !debugger.mouseOver)
                fullscreenComponents.guiEventHandler.mouseWheel(vec(mouseX, mouseY), GuiComponentEvents.MouseWheelDirection.fromSign(wheelAmount))
        }
    }

    fun tick() {
        if(isDebugMode) debugger.guiEventHandler.tick()
        fullscreenComponents.guiEventHandler.tick()
    }
}