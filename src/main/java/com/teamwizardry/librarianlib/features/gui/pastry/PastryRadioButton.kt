package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.CallbackValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid

class PastryRadioButton<T> internal constructor(private val set: PastryRadioButtonSet<T>, value: T) : GuiComponent(0, 0) {
    val labelContainer = ComponentVoid(0, 0)
    val label = PastryLabel()

    val valueFunc = CallbackValue(value)
    var value by valueFunc.Delegate()

    private val icon = ComponentSprite(null, 0, 0)
    private val sprite_on = PastryStyle.getSprite("radio_button_on", PastryStyle.currentStyle.radioAndCheckboxSize, PastryStyle.currentStyle.radioAndCheckboxSize)
    private val sprite_off = PastryStyle.getSprite("radio_button_off", PastryStyle.currentStyle.radioAndCheckboxSize, PastryStyle.currentStyle.radioAndCheckboxSize)

    init {
//        this.layout.shrinkIfPossible = true
//        labelContainer.layout.shrinkIfPossible = true

        this.add(labelContainer, icon)
        labelContainer.add(label)

        val size = PastryStyle.currentStyle.radioAndCheckboxSize
        layout {
            icon.layout.left eq this.layout.left
            icon.layout.top geq this.layout.top
            icon.layout.bottom leq this.layout.bottom
            icon.layout.centerY eq this.layout.centerY
            icon.layout.width eq size
            icon.layout.height eq size

            labelContainer.layout.left eq icon.layout.right + 2
            labelContainer.layout.top geq this.layout.top
            labelContainer.layout.bottom leq this.layout.bottom
            labelContainer.layout.centerY eq this.layout.centerY
            labelContainer.layout.right eq this.layout.right

            label.layout.left eq labelContainer.layout.left
            label.layout.centerY eq labelContainer.layout.centerY
            label.layout.top geq labelContainer.layout.top
            label.layout.bottom leq labelContainer.layout.bottom
            label.layout.right leq labelContainer.layout.right
        }

        updateSprite()
    }

    private var selected: Boolean
        get() = _selected
        set(v) {
            if(v)
                set.select(this.value)
            else
                set.deselectAll()
        }

    internal var _selected = false
        set(value) {
            field = value
            updateSprite()
        }

    private fun updateSprite() {
        if(selected) {
            icon.sprite = sprite_on
        } else {
            icon.sprite = sprite_off
        }
    }

    @Hook
    private fun click(e: GuiComponentEvents.MouseClickEvent) {
        set.select(value)
    }
}