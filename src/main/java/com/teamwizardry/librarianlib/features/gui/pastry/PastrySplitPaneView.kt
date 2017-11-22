package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.component.named
import com.teamwizardry.librarianlib.features.gui.component.supporting.LayoutConstraint
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import no.birkett.kiwi.Strength

/**
 * @property vertical True if the panes should be stacked vertically
 */
class PastrySplitPaneView @JvmOverloads constructor(val vertical: Boolean = false) : GuiComponent(0, 0) {
    var separatorPos: Double
        get() = if(vertical) separator.pos.y else separator.pos.x
        set(value) {
            if(vertical)
                separator.pos = separator.pos.setY(value.clamp(0.0, this.size.y-3))
            else
                separator.pos = separator.pos.setX(value.clamp(0.0, this.size.x-3))
        }
    /**
     * The left or top pane
     */
    var pane1: GuiComponent? = null
        set(value) {
            field?.also {
                _pane1.relationships.remove(it)
                _pane1Constraints.forEach { _pane1.layout.remove(it) }
            }
            field = value
            field?.also {
                _pane1.add(it)
                _pane1Constraints.clear()
                _pane1Constraints.addAll(_pane1.layout.boundsEqualTo(it))
            }
        }
    /**
     * The right or bottom pane
     */
    var pane2: GuiComponent? = null
        set(value) {
            field?.also {
                _pane2.relationships.remove(it)
                _pane2Constraints.forEach { _pane2.layout.remove(it) }
            }
            field = value
            field?.also {
                _pane2.add(it)
                _pane2Constraints.clear()
                _pane2Constraints.addAll(_pane2.layout.boundsEqualTo(it))
            }
        }

    private val _pane1 = ComponentVoid(0, 0)
    private val _pane2 = ComponentVoid(0, 0)

    private val _pane1Constraints = mutableListOf<LayoutConstraint>()
    private val _pane2Constraints = mutableListOf<LayoutConstraint>()
    private val separator = ComponentVoid(0, 0)

    init {
        if(vertical) {
            separator.layout.height eq 3
            separator.layout.left eq this.layout.left
            separator.layout.right eq this.layout.right

            val bgSprite = PastryStyle.getSprite("pane_separator_background_horizontal", 8, 3)
            val middleSprite = PastryStyle.getSprite("pane_separator_middle_horizontal", 8, 3)

            val bgLeft = ComponentSprite(bgSprite, 0, 0)
            bgLeft.layout.left eq separator.layout.left
            bgLeft.layout.top eq separator.layout.top
            bgLeft.layout.bottom eq separator.layout.bottom

            val bgRight = ComponentSprite(bgSprite, 0, 0)
            bgRight.layout.right eq separator.layout.right
            bgRight.layout.top eq separator.layout.top
            bgRight.layout.bottom eq separator.layout.bottom

            val middle = ComponentSprite(middleSprite, 0, 0)
            middle.layout.fixedSize()
            middle.layout.left eq bgLeft.layout.right - 1
            middle.layout.right eq bgRight.layout.left + 1
            middle.layout.top eq separator.layout.top
            middle.layout.bottom eq separator.layout.bottom
            middle.layout.centerX eq separator.layout.centerX

            separator.add(bgLeft, bgRight, middle)

            _pane1.layout.top eq this.layout.top
            _pane1.layout.left eq this.layout.left
            _pane1.layout.right eq this.layout.right
            _pane1.layout.bottom eq separator.layout.top

            _pane2.layout.bottom eq this.layout.bottom
            _pane2.layout.left eq this.layout.left
            _pane2.layout.right eq this.layout.right
            _pane2.layout.top eq separator.layout.bottom

            separator.layout.top.strength = Strength.PREFERRED
            DragMixin(separator) { v ->
                vec(0, v.y.clamp(0.0, this.size.y - 3).toInt())
            }
            separator.render.hoverCursor = LibCursor.RESIZE_UPDOWN
        } else {
            separator.layout.width eq 3
            separator.layout.top eq this.layout.top
            separator.layout.bottom eq this.layout.bottom

            val bgSprite = PastryStyle.getSprite("pane_separator_background_vertical", 3, 8)
            val middleSprite = PastryStyle.getSprite("pane_separator_middle_vertical", 3, 8)

            val bgTop = ComponentSprite(bgSprite, 0, 0)
            bgTop.layout.top eq separator.layout.top
            bgTop.layout.left eq separator.layout.left
            bgTop.layout.right eq separator.layout.right

            val bgBottom = ComponentSprite(bgSprite, 0, 0)
            bgBottom.layout.bottom eq separator.layout.bottom
            bgBottom.layout.left eq separator.layout.left
            bgBottom.layout.right eq separator.layout.right

            val middle = ComponentSprite(middleSprite, 0, 0)
            middle.layout.fixedSize()
            middle.layout.top eq bgTop.layout.bottom - 1
            middle.layout.bottom eq bgBottom.layout.top + 1
            middle.layout.left eq separator.layout.left
            middle.layout.right eq separator.layout.right
            middle.layout.centerY eq separator.layout.centerY

            separator.add(bgTop, bgBottom, middle)

            _pane1.layout.left eq this.layout.left
            _pane1.layout.top eq this.layout.top
            _pane1.layout.bottom eq this.layout.bottom
            _pane1.layout.right eq separator.layout.left

            _pane2.layout.right eq this.layout.right
            _pane2.layout.top eq this.layout.top
            _pane2.layout.bottom eq this.layout.bottom
            _pane2.layout.left eq separator.layout.right

            separator.layout.left.strength = Strength.PREFERRED
            DragMixin(separator) { v ->
                vec(v.x.clamp(0.0, this.size.x - 3).toInt(), 0)
            }
            separator.render.hoverCursor = LibCursor.RESIZE_LEFTRIGHT
        }

        this.add(_pane1, _pane2, separator)
    }
}