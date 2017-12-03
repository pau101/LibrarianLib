package com.teamwizardry.librarianlib.test.container

/*
 * Created by bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.test.gui.tests.GuiFluidTank
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

class FluidTankContainer(player: EntityPlayer, te: TEFluidTank) : ContainerBase(player) {

    val invPlayer = InventoryWrapperPlayer(player)
    val invBlock = FluidTankWrapper(te)

    init {
        addSlots(invPlayer)
        addSlots(invBlock)

        transferRule().from(invPlayer.main).from(invPlayer.hotbar).to(invBlock.input)
        transferRule().from(invBlock.input).from(invBlock.output).to(invPlayer.main).to(invPlayer.hotbar)
    }

    companion object {
        val NAME = ResourceLocation("librarianlibtest", "fluidtankcontainer")

        init {
            GuiHandler.registerTileContainer(NAME, { player, _, tile -> FluidTankContainer(player, tile as TEFluidTank) }, { _, container -> GuiFluidTank(container) })
        }
    }
}

class FluidTankWrapper(inventory: TEFluidTank) : InventoryWrapper(inventory) {
    val input = slots[TEFluidTank.SLOT_IN]
    val output = slots[TEFluidTank.SLOT_OUT]
}
