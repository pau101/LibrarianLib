package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.CallbackValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import no.birkett.kiwi.Strength
import java.awt.Color

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
    private val textComponent = ComponentText(0, 0)

    private val buttonOnSprite = PastryStyle.getSprite("button_on", 5, 5)
    private val buttonOffSprite = PastryStyle.getSprite("button_off", 5, 5)
    private val buttonHoverSprite = PastryStyle.getSprite("button_hover", 5, 5)

    init {
        textComponent.color = Color.WHITE
        textComponent.textFunc.set { this.text }
        updateState()

        add(backgroundComponent, iconComponent, textComponent)
        backgroundComponent.layout.boundsEqualTo(this)
        val margin = 3
        textComponent.layout.boundsEqualTo(this, vec(margin, margin), vec(-margin, -margin))

        iconComponent.layout.left eq backgroundComponent.layout.left + margin
        iconComponent.layout.top eq backgroundComponent.layout.top + margin
        iconComponent.layout.bottom leq backgroundComponent.layout.bottom - margin
        iconComponent.layout.right leq backgroundComponent.layout.right - margin
        iconComponent.layout.width.strength = Strength.STRONG
        iconComponent.layout.height.strength = Strength.STRONG
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
}