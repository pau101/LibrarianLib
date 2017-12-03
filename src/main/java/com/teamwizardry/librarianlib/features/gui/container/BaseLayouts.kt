package com.teamwizardry.librarianlib.features.gui.container

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer
import com.teamwizardry.librarianlib.features.container.SlotBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid

/**
 * Created by TheCodeWarrior
 */
class ComponentGridSlotLayout(inv: List<SlotBase>, rowLength: Int) : GuiComponent() {
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

            val slot = ComponentSlot(inv[index], column * 18, 0)
            rows[row].add(slot)

            slot
        }
    }
}

class ComponentPlayerSlotLayout(player: InventoryWrapperPlayer) : GuiComponent() {
    val armor: ComponentVoid
    val mainWrapper: ComponentVoid
    val main: ComponentVoid
    val hotbar: ComponentVoid
    val offhand: ComponentVoid

    init {
        armor = ComponentVoid(0, 0)
        armor.isVisible = false
        armor.add(
                ComponentSlot(player.head, 0, 0),
                ComponentSlot(player.chest, 0, 18),
                ComponentSlot(player.legs, 0, 2 * 18),
                ComponentSlot(player.feet, 0, 3 * 18)
        )

        offhand = ComponentVoid(0, 0)
        offhand.isVisible = false
        offhand.add(
                ComponentSlot(player.offhand, 0, 0)
        )

        mainWrapper = ComponentVoid(0, 0)

        main = ComponentVoid(0, 0)
        for (row in 0..2) {
            for (column in 0..8) {
                main.add(ComponentSlot(player.main[row * 9 + column], column * 18, row * 18))
            }
        }

        hotbar = ComponentVoid(0, 58)
        for (column in 0..8) {
            hotbar.add(ComponentSlot(player.hotbar[column], column * 18, 0))
        }
        mainWrapper.add(main, hotbar)

        this.add(armor, mainWrapper, offhand)
    }
}
