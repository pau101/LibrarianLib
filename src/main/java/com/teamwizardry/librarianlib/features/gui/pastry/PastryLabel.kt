package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import java.awt.Color

class PastryLabel @JvmOverloads constructor(text: String? = null) : GuiComponent(0, 0) {
    val managedText = ComponentText(0, 0)
    private val pastryStyle = PastryStyle.currentStyle

    var style = Style.DARK
        set(value) {
            field = value
            managedText.color = pastryStyle.textColors[style] ?: Color.BLACK
        }
    var shadow by managedText::shadow.delegate

    val textFunc by managedText::textFunc.delegate
    var text by managedText::text.delegate

    init {
        this.add(managedText)
        layout {
            managedText.layout.boundsEqualTo(this)
        }
        text?.also { this.text = it }
    }

    enum class Style {
        LIGHT, DARK
    }
}
