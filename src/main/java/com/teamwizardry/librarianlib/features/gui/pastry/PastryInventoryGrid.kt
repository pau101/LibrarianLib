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

    val rows = Array((inv.size + rowLength - 1) / rowLength) { i ->
        val row = PastryInventoryRow(inv, i * rowLength .. Math.min((i+1)*rowLength, inv.size-1))
        row.layout.left eq this.layout.left
        row.layout.right eq this.layout.right
        this.add(row)
        row
    }
    val slots = rows.flatMap { it.slots }

    init {
        this.layout.top eq rows.first().layout.top
        this.layout.bottom eq rows.last().layout.bottom
    }
}