package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer

class PastryInventoryPlayer(player: InventoryWrapperPlayer) : GuiComponent() {
    val armor: ComponentVoid
    val mainWrapper: ComponentVoid
    val main: ComponentVoid
    val hotbar: ComponentVoid
    val offhand: ComponentVoid

    init {
        armor = ComponentVoid(0, 0)
        armor.isVisible = false
        armor.add(
                PastrySlot(player.head, 0, 0),
                PastrySlot(player.chest, 0, 18),
                PastrySlot(player.legs, 0, 2 * 18),
                PastrySlot(player.feet, 0, 3 * 18)
        )

        offhand = ComponentVoid(0, 0)
        offhand.isVisible = false
        offhand.add(
                PastrySlot(player.offhand, 0, 0)
        )

        mainWrapper = ComponentVoid(0, 0)

        main = ComponentVoid(0, 0)
        for (row in 0..2) {
            for (column in 0..8) {
                main.add(PastrySlot(player.main[row * 9 + column], column * 18, row * 18))
            }
        }

        hotbar = ComponentVoid(0, 58)
        for (column in 0..8) {
            hotbar.add(PastrySlot(player.hotbar[column], column * 18, 0))
        }
        mainWrapper.add(main, hotbar)

        this.add(armor, mainWrapper, offhand)
    }
}
