package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Created by TheCodeWarrior
 */
class PastrySlot(val slot: SlotBase, useBackground: Boolean = true) : GuiComponent() {
    private val inner = ComponentVoid(0, 0)
    private val background = ComponentSprite(PastryStyle.getSprite("slot_background", PastryStyle.currentStyle.slotBackgroundSize.xi, PastryStyle.currentStyle.slotBackgroundSize.yi), 0, 0)
    private val iconComponent = ComponentSprite(null, 0, 0)

    var icon by iconComponent::sprite.delegate

    init {
        if(useBackground) {

            inner.layout.fixedSize()
            inner.size = vec(16, 16)
            inner.layout.boundsEqualTo(background, PastryStyle.currentStyle.slotBackgroundMargins)
//            this.layout.boundsEqualTo(inner)
            this.layout.boundsEqualTo(background)
            iconComponent.layout.boundsEqualTo(inner)
            this.add(background, iconComponent, inner)
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
