package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import java.awt.Color

class ComponentText @JvmOverloads constructor(posX: Int, posY: Int, horizontal: ComponentText.TextAlignH = ComponentText.TextAlignH.LEFT, vertical: ComponentText.TextAlignV = ComponentText.TextAlignV.TOP) : GuiComponent(posX, posY) {

    /**
     * Set to true to enable wrapping to bounds
     */
    var wrapText = false
    /**
     * Text alignment on the X axis.
     *
     * -1 = left, 0 = center, 1 = right (mnemonic: The direction on the X axis to align to)
     */
    var xAlign = horizontal.ordinal - 1
    /**
     * Shorthand for centering text horizontally
     */
    fun centerText() { xAlign = 0 }
    /**
     * Text alignment on the Y axis.
     *
     * -1 = top, 0 = middle, 1 = bottom (mnemonic: The direction on the Y axis to align to)
     */
    var yAlign = vertical.ordinal - 1

    /**
     * The text to draw
     */
    val text = Option<ComponentText, String>("-NULL TEXT-")
    /**
     * The color of the text
     */
    val color = Option<ComponentText, Color>(Color.BLACK)
    /**
     * The wrap width in pixels, -1 for no wrapping
     */
    @Deprecated("Set `wrapText` to true and set the component's width")
    val wrap = Option<ComponentText, Int>(-1)
    /**
     * Whether to set the font renderer's unicode and bidi flags
     */
    val unicode = Option<ComponentText, Boolean>(false)
    /**
     * Whether to render a shadow behind the text
     */
    val shadow = Option<ComponentText, Boolean>(false)

    /**
     * Set the text value and unset the function
     */
    fun `val`(str: String): ComponentText {
        text.setValue(str)
        text.noFunc()
        return this
    }

    /**
     * Set the callback to create the text for

     * @param func
     * *
     * @return
     */
    fun func(func: (ComponentText) -> String): ComponentText {
        text.func(func)
        return this
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val fr = Minecraft.getMinecraft().fontRenderer

        val fullText = text.getValue(this)
        val colorHex = color.getValue(this).rgb
        val enableFlags = unicode.getValue(this)
        val dropShadow = shadow.getValue(this)

        if (enableFlags) {
            fr.bidiFlag = true
            fr.unicodeFlag = true
        }

        val x = 0
        var y = 0

        val lines: List<String>

        var wrap = this.wrap.getValue(this)
        if(wrap == -1 && wrapText) {
            wrap = this.size.xi
        }
        if (wrap == -1) {
            lines = listOf(fullText)
        } else {
            lines = try {
                fr.listFormattedStringToWidth(fullText, wrap)
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
        var wrap = this.wrap.getValue(this)
        if(wrapText) {
            wrap = size.xi
        }

        var size: Vec2d

        val fr = Minecraft.getMinecraft().fontRenderer

        val enableFlags = unicode.getValue(this)

        if (enableFlags) {
            fr.unicodeFlag = true
            fr.bidiFlag = true
        }

        if (wrap == -1) {
            size = vec(fr.getStringWidth(text.getValue(this)), fr.FONT_HEIGHT)
        } else {
            val wrapped = try {
                fr.listFormattedStringToWidth(text.getValue(this), wrap)
            } catch(e: StackOverflowError) {
                listOf(text.getValue(this))
            }
            size = vec(wrap, wrapped.size * fr.FONT_HEIGHT)
        }

        if (enableFlags) {
            fr.unicodeFlag = false
            fr.bidiFlag = false
        }

        if(wrapText) {
            size = size.setX(this.size.xi.toDouble())
        }
        return size
    }

    @Deprecated("This is now automatic and on by default")
    fun sizeToText() {
        this.size = contentSize.size
    }

    @Deprecated("Use `getImplicitSize()`")
    val contentSize: BoundingBox2D
        get() {
            val wrap = this.wrap.getValue(this)

            val size: Vec2d

            val fr = Minecraft.getMinecraft().fontRenderer

            val enableFlags = unicode.getValue(this)

            if (enableFlags) {
                fr.unicodeFlag = true
                fr.bidiFlag = true
            }

            if (wrap == -1) {
                size = vec(fr.getStringWidth(text.getValue(this)), fr.FONT_HEIGHT)
            } else {
                val wrapped = fr.listFormattedStringToWidth(text.getValue(this), wrap)
                size = vec(wrap, wrapped.size * fr.FONT_HEIGHT)
            }

            if (enableFlags) {
                fr.unicodeFlag = false
                fr.bidiFlag = false
            }

            return BoundingBox2D(Vec2d.ZERO, size)
        }

    enum class TextAlignH {
        LEFT, CENTER, RIGHT
    }

    enum class TextAlignV {
        TOP, MIDDLE, BOTTOM
    }

}
