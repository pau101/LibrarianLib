package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.*
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.plus
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ComponentTextInput(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentTextInput>(posX, posY, width, height) {
    class TextChangeEvent<T : GuiComponent<*>>(val component: T, val oldText: String, var newText: String) : EventCancelable()

    val color = Value<Color>(Color.BLACK)
    var text = ""
        set(value) {
            val value_ = value.filter { !isControlCharacter(it) }
            val event = BUS.fire(TextChangeEvent(thiz(), field, value_))
            if(!event.isCanceled())
                field = event.newText
        }
    var cursor = 0
        set(value) {
            if(value < 0)
                field = 0
            else if(value > text.length-1)
                field = text.length-1
            else
                field = value
        }
    var selection = 0
    val wrapManager = WrappedStringManager(text, size.xi)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        wrapManager.draw(pos.xi, pos.yi, Color.BLACK)
        var cursorPos = pos + wrapManager.getPosFromIndex(cursor) - Vec2d.X
        if(cursorPos.x < 0)
            cursorPos = cursorPos.setX(0.0)
        val cursorSize = Vec2d(1, 9)

        val shear = 0

        val tessellator = Tessellator.getInstance()
        val vertexbuffer = tessellator.buffer

        GlStateManager.disableTexture2D()
        GlStateManager.color(0f, 0f, 0f, 1f)
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION)

        vertexbuffer.pos(cursorPos.x                - shear, cursorPos.y + cursorSize.y, 0.0).endVertex()
        vertexbuffer.pos(cursorPos.x + cursorSize.x - shear, cursorPos.y + cursorSize.y, 0.0).endVertex()
        vertexbuffer.pos(cursorPos.x + cursorSize.x + shear, cursorPos.y               , 0.0).endVertex()
        vertexbuffer.pos(cursorPos.x                + shear, cursorPos.y               , 0.0).endVertex()

        tessellator.draw()

        GlStateManager.enableTexture2D()
    }

    init {

        BUS.hook(SetSizeEvent::class.java) { event ->
            wrapManager.width = event.size.xi
        }

        BUS.hook(MouseClickEvent::class.java) { event ->
            if(mouseOver) {
                this.focused = true

                cursor = wrapManager.getIndexFromPos(event.mousePos.xi, event.mousePos.yi)
            }
        }

        BUS.hook(MouseUpEvent::class.java) { event ->
            // mouse was clicked and un-clicked outside the component
            if(!mouseOver && !mouseButtonsDown[event.button.ordinal]) {
                this.focused = false
            }
        }

        BUS.hook(KeyDownEvent::class.java) { event ->
            if(this.focused)
                typeKey(event.key, event.keyCode)
        }
    }

    fun typeKey(key: Char, keyCode: Int) {
        var handled = false

        if(GuiScreen.isKeyComboCtrlA(keyCode)) {
            // TODO: Implement selections
        }

        if(GuiScreen.isKeyComboCtrlC(keyCode)) {
            // TODO: Implement copying
        }

        if(GuiScreen.isKeyComboCtrlV(keyCode)) {
            // TODO: Implement pasting
        }

        when(keyCode) {
            Keyboard.KEY_BACK -> { deleteString(-1); handled = true; }
            Keyboard.KEY_DELETE -> { deleteString(1); handled = true; }

            Keyboard.KEY_LEFT -> { cursor--; handled = true; }
            Keyboard.KEY_RIGHT -> { cursor++; handled = true; }

            Keyboard.KEY_UP -> {
                val cursorPos = wrapManager.getPosFromIndex(cursor) - Vec2d(0, wrapManager.fontHeight)
                val newIndex = wrapManager.getIndexFromPos(cursorPos.xi, cursorPos.yi)
                cursor = newIndex
                handled = true
            }
            Keyboard.KEY_DOWN -> {
                val cursorPos = wrapManager.getPosFromIndex(cursor) + Vec2d(0, wrapManager.fontHeight)
                val newIndex = wrapManager.getIndexFromPos(cursorPos.xi, cursorPos.yi)
                cursor = newIndex
                handled = true
            }

            Keyboard.KEY_RETURN -> { insertString("\n"); handled = true; }
        }

        if(!handled && !isControlCharacter(key))
            insertString("" + key)
    }

    /**
     * Deletes a string ahead (length > 0) or behind (length < 0) the cursor
     */
    fun deleteString(length: Int) {
        if(length < 0) {
            // + length because length is < 0
            deleteAt(cursor+length, -length)
            cursor += length
        } else {
            deleteAt(cursor, length)
        }
    }

    fun insertString(string: String) {
        insertAt(cursor, string)
        cursor += string.length
    }

    fun deleteAt(loc: Int, length: Int) {
        var loc_ = loc
        var len_ = length
        if(loc < 0) {
            loc_ = 0
            len_ += loc // loc is negative, so we add it
        }
        text = text.substring(0, loc_) + text.substring(loc_+len_)
    }

    fun insertAt(loc: Int, string: String) {
        text = text.substring(0, loc) + string + text.substring(loc)
    }

    private fun isControlCharacter(char_: Char): Boolean {
        val char = char_.toInt() // just so I don't have to do .toChar() on all the constants

        if(
            char == 9 || // tab
            char == 10 || // linefeed
            char == 13    // carrige return
        )
            return false
        if(char < 32)
            return true
        if(char == 127)
            return true
        return false
    }
}