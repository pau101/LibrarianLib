package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import java.util.*

class PastryBackground() : GuiComponent(0, 0) {
    private val background = ComponentSprite(getSprite(), 0, 0)

    init {
        this.add(background)
        background.zIndex = -1000

        background.layout.boundsEqualTo(this, vec(-3, -3), vec(3, 3))
    }

    private fun getSprite(): Sprite {
        val name = "background"
        return PastryStyle.getSprite(name, 12, 12)
    }
}