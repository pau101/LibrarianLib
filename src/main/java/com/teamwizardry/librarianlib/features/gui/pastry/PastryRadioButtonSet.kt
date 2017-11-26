package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.eventbus.EventBus

class PastryRadioButtonSet<T>(private val callback: (value: T?) -> Unit) {
    private val buttons = mutableListOf<PastryRadioButton<T>>()
    var selected: T? = null
        private set(value) {
            field = value
            callback(value)
        }

    @JvmOverloads
    fun createButton(value: T, label: String? = null): PastryRadioButton<T> {
        val button = PastryRadioButton(this, value)
        if(label != null) {
            button.label.text = label
        }
        buttons.add(button)
        return button
    }

    fun select(value: T) {
        selected = value
        buttons.forEach {
            it._selected = it.value == value
        }
    }

    fun deselectAll() {
        selected = null
        buttons.forEach {
            it._selected = false
        }
    }
}