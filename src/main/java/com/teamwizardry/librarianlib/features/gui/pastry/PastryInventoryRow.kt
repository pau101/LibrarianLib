package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec

class PastryInventoryRow @JvmOverloads constructor(inv: List<SlotBase>, range: IntRange = 0 until inv.size, buffer: Int = 0, gap: Int = 0, reverse: Boolean = false) : GuiComponent() {
    @JvmOverloads constructor(inv: InventoryWrapper, range: IntRange = 0 until inv.all.size, buffer: Int = 0, gap: Int = 0, reverse: Boolean = false) : this(inv.all, range, buffer, gap, reverse)

    val slots = range.map { PastrySlot(inv[it], buffer) }

    init {
        this.geometry.integerBounds = true

        this.layout.bake()
        this.add(*slots.toTypedArray())

        var x = 0.0
        var height = 0.0
        val list = (if(reverse) slots.reversed() else slots)

        list.forEach { slot ->
            slot.pos = vec(x, 0)
            height = Math.max(height, slot.size.y)
            x += slot.size.x + gap
        }
        this.size = vec(x, height)
    }
}
