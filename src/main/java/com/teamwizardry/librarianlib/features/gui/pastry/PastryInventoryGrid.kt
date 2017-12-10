package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.pastry.PastrySlot
import com.teamwizardry.librarianlib.features.helpers.vec

/**
 * Created by TheCodeWarrior
 */
class PastryInventoryGrid @JvmOverloads constructor(inv: List<SlotBase>, rowLength: Int, range: IntRange = 0 until inv.size,
                          buffer: Int = 0, gap: Int = 0, reverseX: Boolean = false, reverseY: Boolean = false) : GuiComponent() {
    @JvmOverloads constructor(inv: InventoryWrapper, rowLength: Int, range: IntRange = 0 until inv.all.size,
                buffer: Int = 0, gap: Int = 0, reverseX: Boolean = false, reverseY: Boolean = false)
            : this(inv.all, rowLength, range, buffer, gap, reverseX, reverseY)

    val slots = range.map { PastrySlot(inv[it], buffer) }

    init {
        this.geometry.integerBounds = true

        this.layout.bake()
        this.add(*slots.toTypedArray())

        val segmentCount = (slots.size + rowLength-1)/rowLength
        var segments = (0 until segmentCount).map { segment ->
            val l = slots.subList(rowLength * segment, Math.min(slots.size, rowLength*(segment+1)) )
            if(reverseX) {
                return@map l.reversed()
            } else {
                return@map l
            }
        }
        if(reverseY) {
            segments = segments.reversed()
        }

        var maxX = 0.0
        var maxY = 0.0

        var x = 0.0
        var y = 0.0

        segments.forEach { row ->
            row.forEach { slot ->
                slot.pos = vec(x, y)
                x += slot.size.x + gap
                maxY = Math.max(maxY, slot.pos.y + slot.size.y)
                maxX = Math.max(maxX, slot.pos.x + slot.size.x)
            }
            x = 0.0
            y = maxY + gap
        }

        this.size = vec(maxX, maxY)
    }
}
