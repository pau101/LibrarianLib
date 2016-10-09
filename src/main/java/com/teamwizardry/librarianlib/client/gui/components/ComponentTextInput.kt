package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.font.GlyphLayout
import com.teamwizardry.librarianlib.client.font.StringRenderer
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
import org.apache.commons.lang3.StringUtils
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
            if(!event.isCanceled()) {
                field = event.newText
                stringRenderer.text = event.newText
            }
        }
    var cursor = 0
        set(value) {
            if(value < 0)
                field = 0
            else if(value > text.length)
                field = if(text.length == 0) 0 else text.length
            else
                field = value
        }
    var selection = 0
    val stringRenderer = StringRenderer()
    val popupStringRenderer = StringRenderer()
    var enableFormatting: Boolean
        set(value) { stringRenderer.enableFormatCodes = value }
        get() = stringRenderer.enableFormatCodes

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        stringRenderer.render(pos.xi, pos.yi+stringRenderer.fontSize)
        val cursorGlyph = stringRenderer.getGlyphFor(cursor)
        if(cursorGlyph != null) {
            val cursorPos = Vec2d(cursorGlyph.x/StringRenderer.screenScale+pos.xi, cursorGlyph.y/StringRenderer.screenScale+pos.yi)
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

            var sectionSymbolPos = cursor
            while(sectionSymbolPos >= 0) {
                if(sectionSymbolPos < text.length && text[sectionSymbolPos] == '§')
                    break
                sectionSymbolPos--
            }

            if(sectionSymbolPos > 0) {
                val len = GlyphLayout.validFormatLength(text, sectionSymbolPos+1)
                val cursorInsidePos = cursor-sectionSymbolPos+1

                if(len > 0 && cursorGlyph.stringIndex in sectionSymbolPos..(sectionSymbolPos+len+1)) {

                    val format = text.substring(sectionSymbolPos, Math.min(sectionSymbolPos+len+1, text.length))
                    popupStringRenderer.text = format.replace("§", "§ ")

                    val afterChar = stringRenderer.getGlyphFor(Math.min(sectionSymbolPos+len, text.length-1))
                    if(afterChar != null) {
                        val popupRenderPos = Vec2d(pos.xi+afterChar.x.toInt()/StringRenderer.screenScale, pos.yi+(afterChar.y.toInt() + stringRenderer.fontSize) / StringRenderer.screenScale)
                        popupStringRenderer.render(popupRenderPos.xi, popupRenderPos.yi)

                        val popupGlyph = popupStringRenderer.getGlyphFor(cursorInsidePos)
                        if(popupGlyph != null) {
                            val popupPos = Vec2d(popupRenderPos.xi + popupGlyph.x / StringRenderer.screenScale, popupRenderPos.yi + popupGlyph.y / StringRenderer.screenScale - popupStringRenderer.fontSize)

                            GlStateManager.disableTexture2D()
                            GlStateManager.color(0f, 0f, 0f, 1f)
                            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR)

                            vertexbuffer.pos(popupPos.x - shear, popupPos.y + cursorSize.y, 0.0).color(0f, 0f, 0f, 1f).endVertex()
                            vertexbuffer.pos(popupPos.x + cursorSize.x - shear, popupPos.y + cursorSize.y, 0.0).color(0f, 0f, 0f, 1f).endVertex()
                            vertexbuffer.pos(popupPos.x + cursorSize.x + shear, popupPos.y, 0.0).color(0f, 0f, 0f, 1f).endVertex()
                            vertexbuffer.pos(popupPos.x + shear, popupPos.y, 0.0).color(0f, 0f, 0f, 1f).endVertex()

                            tessellator.draw()

                            GlStateManager.enableTexture2D()
                        }
                    }
                }
            }

        }
    }

    init {

        popupStringRenderer.fontSize = 8
        stringRenderer.fontSize = 8
        stringRenderer.wrap = size.xi

        BUS.hook(SetSizeEvent::class.java) { event ->
            stringRenderer.wrap = event.size.xi
        }

        BUS.hook(MouseClickEvent::class.java) { event ->
            if(mouseOver) {
                if(this.focused) {
                    cursor = stringRenderer.getGlyphForClickPos(event.mousePos.xi*StringRenderer.screenScale, event.mousePos.yi*StringRenderer.screenScale)?.stringIndex ?: 0
                }
                this.focused = true
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
                val cursorGlyph = stringRenderer.getGlyphFor(cursor)
                if(cursorGlyph == null) {
                    cursor = 0
                } else {
                    cursor = stringRenderer.getGlyphForClickPos(cursorGlyph.x.toInt(), cursorGlyph.y.toInt()-stringRenderer.fontSize)?.stringIndex ?: 0
                }
                handled = true
            }
            Keyboard.KEY_DOWN -> {
                val cursorGlyph = stringRenderer.getGlyphFor(cursor)
                if(cursorGlyph == null) {
                    cursor = 0
                } else {
                    cursor = stringRenderer.getGlyphForClickPos(cursorGlyph.x.toInt(), cursorGlyph.y.toInt()+stringRenderer.fontSize)?.stringIndex ?: 0
                }
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
        var length = length
        if(length < 0) {
            length *= -1
            deleteAt(cursor-length, length)
            cursor -= length
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