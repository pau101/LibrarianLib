package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.pastry.PastrySlot

/**
 * Created by TheCodeWarrior
 */
class PastryInventoryGrid(inv: List<SlotBase>, rowLength: Int) : GuiComponent() {
    constructor(inv: InventoryWrapper, rowLength: Int) : this(inv.all, rowLength)

    val rowCount = (inv.size + rowLength - 1) / rowLength
    val rows = Array(rowCount) {
        val row = ComponentVoid(0, it * 18)
        this.add(row)
        row
    }
    val slots = Array(rowCount) { row ->
        Array(if (row == rowCount - 1) inv.size - rowLength * row else rowLength) { column ->
            val index = row * rowLength + column

            val slot = PastrySlot(inv[index], column * 18, 0)
            rows[row].add(slot)

            slot
        }
    }
}