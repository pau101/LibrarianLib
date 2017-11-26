package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * @property map The location of the spritesheet for this style
 *
 * @property buttonMargins The margins between the edges of the button sprite and the button content
 * @property buttonSize The size in pixels of the button texture
 * @property buttonTextStyle The style of text to be used for button labels
 * @property buttonTextShadow Whether button labels should have shadows
 *
 * @property backgroundMargins The margins between the edges of the background sprite and the background content
 * @property backgroundSize The size in pixels of the background texture
 *
 * @property textColors A map of text style to text color
 *
 * @property splitPaneDividerThickness The thickness in pixels of the divider in a [PastrySplitPaneView]
 * @property splitPaneKnobHeight The height in pixels (along the non-thickness axis) of the split pane view knob texture
 *
 * @property radioAndCheckboxSize The width and height in pixels of the radio button and checkbox sprites
 */
class PastryStyle(
        val map: ResourceLocation,

        val buttonMargins: Margins,
        val buttonSize: Vec2d,
        val buttonTextStyle: PastryLabel.Style,
        val buttonTextShadow: Boolean,

        val backgroundMargins: Margins,
        val backgroundSize: Vec2d,

        val textColors: Map<PastryLabel.Style, Color>,

        val splitPaneDividerThickness: Int,
        val splitPaneKnobHeight: Int,

        val radioAndCheckboxSize: Int
) {

    val texture = Texture(map)

    companion object {
        @JvmStatic val VANILLA = PastryStyle(
                map = "librarianlib:textures/libgui/styles/vanilla.png".toRl(),

                buttonMargins = Margins(3, 3, 3, 3),
                buttonSize = vec(5, 5),
                buttonTextStyle = PastryLabel.Style.LIGHT,
                buttonTextShadow = false,

                backgroundMargins = Margins(3, 3, 3, 3),
                backgroundSize = vec(12, 12),

                textColors = mapOf(
                        PastryLabel.Style.LIGHT to Color.WHITE,
                        PastryLabel.Style.DARK to Color(0x404040)
                ),

                splitPaneDividerThickness = 3,
                splitPaneKnobHeight = 8,

                radioAndCheckboxSize = 8
        )

        var currentStyle: PastryStyle = VANILLA
            private set

        fun useStyle(style: PastryStyle) {
            currentStyle = style
        }

        fun getSprite(name: String, w: Int, h: Int): Sprite {
            return currentStyle.texture.getSprite(name, w, h)
        }
    }

}