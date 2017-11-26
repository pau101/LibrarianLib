package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.CallbackValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite

class PastryButton() : GuiComponent(0, 0) {
    constructor(text: String) : this() {
        this.text = text
    }
    constructor(sprite: Sprite) : this() {
        this.icon = sprite
    }

    var enabled = true

    var textFunc = CallbackValue("")
    var text by textFunc.Delegate()

    var icon: Sprite? = null
        set(value) {
            field = value
            iconComponent.sprite = value
            if(value != null) {
                iconComponent.size = vec(value.inWidth, value.inHeight)
            } else {
                iconComponent.size = Vec2d.ZERO
            }
        }

    private val iconComponent = ComponentSprite(null,  0, 0, 0, 0)
    private val backgroundComponent = ComponentSprite(null,  0, 0)
    private val label = PastryLabel()

    private val buttonOnSprite = stateSprite("on")
    private val buttonOffSprite = stateSprite("off")
    private val buttonHoverSprite = stateSprite("hover")

    init {
        label.style = PastryStyle.currentStyle.buttonTextStyle
        label.textFunc.set { this.text }
        updateState()

        add(backgroundComponent, iconComponent, label)
        backgroundComponent.layout.boundsEqualTo(this)
        val margin = PastryStyle.currentStyle.buttonMargins
        label.layout.boundsEqualTo(this, margin)

        iconComponent.layout.left eq this.layout.left + margin.left
        iconComponent.layout.top eq this.layout.top + margin.top
        iconComponent.layout.right leq this.layout.right - margin.right
        iconComponent.layout.bottom leq this.layout.bottom - margin.bottom

        iconComponent.layout.fixedSize()
    }

    private fun updateState() {
        if(!enabled) {
            backgroundComponent.sprite = buttonOffSprite
        } else if(mouseOver) {
            backgroundComponent.sprite = buttonHoverSprite
        } else {
            backgroundComponent.sprite = buttonOnSprite
        }
    }

    @Hook
    private fun updateStateHook(e: GuiComponentEvents.PreDrawEvent) { updateState() }

    private fun stateSprite(state: String): Sprite {
        val size = PastryStyle.currentStyle.buttonSize
        return PastryStyle.getSprite("button_$state", size.xi, size.yi)
    }
}