package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec

class PastryInventoryColumn @JvmOverloads constructor(inv: List<SlotBase>, range: IntRange = 0 until inv.size, buffer: Int = 0, gap: Int = 0, reverse: Boolean = false) : GuiComponent() {
    @JvmOverloads constructor(inv: InventoryWrapper, range: IntRange = 0 until inv.all.size, buffer: Int = 0, gap: Int = 0, reverse: Boolean = false) : this(inv.all, range, buffer, gap, reverse)

    val slots = range.map { PastrySlot(inv[it], buffer) }

    init {
        this.geometry.integerBounds = true

        this.layout.bake()
        this.add(*slots.toTypedArray())

        var y = 0.0
        var width = 0.0
        val list = (if(reverse) slots.reversed() else slots)

        list.forEach { slot ->
            slot.pos = vec(0, y)
            width = Math.max(width, slot.size.x)
            y += slot.size.y + gap
        }
        this.size = vec(width, y)
    }
}
