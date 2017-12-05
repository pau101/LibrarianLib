package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent

class PastryInventoryColumn @JvmOverloads constructor(inv: List<SlotBase>, range: IntRange = 0 until inv.size, gap: Int = 0, reverse: Boolean = false) : GuiComponent() {
    @JvmOverloads constructor(inv: InventoryWrapper, range: IntRange = 0 until inv.all.size, gap: Int = 0, reverse: Boolean = false) : this(inv.all, range, gap, reverse)

    val slots: List<PastrySlot>

    init {
        var prev = if(reverse) this.layout.bottom else this.layout.top
        slots = range.map { i ->
            val slot = PastrySlot(inv[i])

            slot.layout.left eq this.layout.left
            slot.layout.right eq this.layout.right
            if(reverse) {
                slot.layout.bottom eq prev - gap
                prev = slot.layout.top
            } else {
                slot.layout.top eq prev + gap
                prev = slot.layout.bottom
            }

            this.add(slot)

            slot
        }
        if(reverse)
            this.layout.top eq prev
        else
            this.layout.bottom eq prev

    }
}