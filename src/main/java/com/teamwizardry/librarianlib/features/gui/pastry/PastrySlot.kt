package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d
import no.birkett.kiwi.Strength

/**
 * Created by TheCodeWarrior
 */
class PastrySlot @JvmOverloads constructor(val slot: SlotBase, buffer: Int = 0, useBackground: Boolean = true) : GuiComponent() {
    private val inner = ComponentVoid(0, 0)
    private val background = ComponentSprite(PastryStyle.getSprite("slot_background", PastryStyle.currentStyle.slotBackgroundSize.xi, PastryStyle.currentStyle.slotBackgroundSize.yi), 0, 0)
    private val iconComponent = ComponentSprite(null, 0, 0)

    var icon by iconComponent::sprite.delegate

    init {
        this.layout.bake()

        if(useBackground) {
            this.add(background, iconComponent, inner)

            val margins = PastryStyle.currentStyle.slotBackgroundMargins

            inner.size = vec(16, 16)
            this.size = inner.size + vec(margins.left + margins.right + buffer*2, margins.top + margins.bottom + buffer*2)
            background.size = this.size
            iconComponent.size = inner.size

            inner.pos = vec(margins.left + buffer, margins.top + buffer)
            iconComponent.pos = inner.pos
        } else {
            this.add(inner)
            this.layout.boundsEqualTo(inner)
        }
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        iconComponent.isVisible = slot.stack.isEmpty
        slot.visible = true

        val p = inner.thisPosToOtherContext(null)

        slot.xPos = p.xi
        slot.yPos = p.yi
    }
}
