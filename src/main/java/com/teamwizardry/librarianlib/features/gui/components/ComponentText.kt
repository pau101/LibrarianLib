package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.CallbackValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import java.awt.Color

class ComponentText constructor(posX: Int, posY: Int) : GuiComponent(posX, posY) {

    /** center lines on the X axis */
    fun centered(): ComponentText { xAlign = 0; return this }
    /** left-align the text */
    fun leftAligned(): ComponentText { xAlign = -1; return this }
    /** right-align the text */
    fun rightAligned(): ComponentText { xAlign = 1; return this }

    /** center the text on the Y axis */
    fun centerY(): ComponentText { yAlign = 0; return this }
    /** align the top of the text to the top of the bounds */
    fun topAligned(): ComponentText { yAlign = -1; return this }
    /** align the bottom of the text to the bottom of the bounds */
    fun bottomAligned(): ComponentText { yAlign = 1; return this }


    /** Set to true to enable wrapping to component width */
    var wrap = false

    /** Used to set a callback in lieu of [text] */
    val textFunc = CallbackValue("")
    /** The text to draw */
    var text by textFunc.Delegate()
    /** The color of the text */
    var color = Color.BLACK
    /** Whether to set the font renderer's unicode and bidi flags */
    var unicode = false
    /** Whether to render a shadow behind the text */
    var shadow = false

    private var xAlign = -1
    private var yAlign = -1

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val fr = Minecraft.getMinecraft().fontRenderer

        val fullText = text
        val colorHex = color.rgb
        val enableFlags = unicode
        val dropShadow = shadow

        if (enableFlags) {
            fr.bidiFlag = true
            fr.unicodeFlag = true
        }

        val x = 0
        var y = 0

        val lines: List<String>

        if (!wrap) {
            lines = listOf(fullText)
        } else {
            lines = try {
                fr.listFormattedStringToWidth(fullText, this.size.xi)
            } catch(e: StackOverflowError) {
                listOf(fullText)
            }
        }


        val height = lines.size * fr.FONT_HEIGHT
        if (yAlign == 0) {
            y -= height / 2
        } else if (yAlign == 1) {
            y -= height
        }

        for ((i, line) in lines.withIndex()) {
            var lineX = x
            val lineY = y + i * fr.FONT_HEIGHT

            val textWidth = fr.getStringWidth(line)
            if (xAlign == 0) {
                lineX -= textWidth / 2
                lineX += (this.size.x / 2).toInt()
            } else if (xAlign == 1) {
                lineX -= textWidth
            }

            fr.drawString(line, lineX.toFloat(), lineY.toFloat(), colorHex, dropShadow)
        }

        if (enableFlags) {
            fr.bidiFlag = false
            fr.unicodeFlag = false
        }
    }

    override fun getImplicitSize() : Vec2d? {
        var size: Vec2d

        val fr = Minecraft.getMinecraft().fontRenderer

        val enableFlags = unicode

        if (enableFlags) {
            fr.unicodeFlag = true
            fr.bidiFlag = true
        }

        val text = text
        if (!wrap) {
            size = vec(fr.getStringWidth(text)-1, fr.FONT_HEIGHT)
        } else {
            val wrapped = try {
                fr.listFormattedStringToWidth(text, this.size.xi)
            } catch(e: StackOverflowError) {
                listOf(text)
            }
            size = vec(this.size.xi, wrapped.size * fr.FONT_HEIGHT)
        }

        if (enableFlags) {
            fr.unicodeFlag = false
            fr.bidiFlag = false
        }

        if(wrap) {
            size = size.setX(this.size.xi.toDouble())
        }
        return size
    }

}
