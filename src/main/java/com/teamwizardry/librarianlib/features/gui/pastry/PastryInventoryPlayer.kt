package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer

class PastryInventoryPlayer(player: InventoryWrapperPlayer) {
    /**
     * When first accessed, this property adds [main] and [hotbar] to itself.
     */
    val inventory by lazy {
        val c = ComponentVoid(0, 0)
        c.add(main, hotbar)
        hotbar.layout.top eq main.layout.bottom + 4

        main.layout.top eq c.layout.top
        main.layout.left eq c.layout.left
        main.layout.right eq c.layout.right

        hotbar.layout.bottom eq c.layout.bottom
        hotbar.layout.left eq c.layout.left
        hotbar.layout.right eq c.layout.right
        c
    }

    val main: PastryInventoryGrid
    val hotbar: PastryInventoryRow

    val armor: PastryInventoryColumn
    val offhand: PastrySlot

    init {
        armor = PastryInventoryColumn(player.armor, reverse = true)

        offhand = PastrySlot(player.offhand)

        main = PastryInventoryGrid(player.main, 9)

        hotbar = PastryInventoryRow(player.hotbar)

    }
}
