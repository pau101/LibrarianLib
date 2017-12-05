package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent

class PastryInventoryRow @JvmOverloads constructor(inv: List<SlotBase>, range: IntRange = 0 until inv.size, gap: Int = 0, reverse: Boolean = false) : GuiComponent() {
    @JvmOverloads constructor(inv: InventoryWrapper, range: IntRange = 0 until inv.all.size, gap: Int = 0, reverse: Boolean = false) : this(inv.all, range, gap, reverse)

    val slots: List<PastrySlot>

    init {
        var prev = if(reverse) this.layout.right else this.layout.left
//        this.layout.height eq 18
        slots = range.map { i ->
            val slot = PastrySlot(inv[i])

            slot.layout.top eq this.layout.top
            slot.layout.bottom eq this.layout.bottom
            if(reverse) {
                slot.layout.right eq prev - gap
//                slot.layout.left eq gap*i
                prev = slot.layout.left
            } else {
                slot.layout.left eq prev + gap
//                slot.layout.left eq gap*i
                prev = slot.layout.right
            }

            this.add(slot)

            slot
        }
        if(reverse)
            this.layout.left eq prev
        else
            this.layout.right eq prev
    }
}
