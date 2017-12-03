package com.teamwizardry.librarianlib.features.container.builtin

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

/**
 * Created by TheCodeWarrior
 */
class InventoryWrapperPlayer(val player: EntityPlayer) : InventoryWrapper(player.inventory) {

    val armor = slots[36..39]
    val head = slots[39]
    val chest = slots[38]
    val legs = slots[37]
    val feet = slots[36]

    val hotbar = slots[0..8]
    val main = slots[9..35]
    val offhand = slots[40]

    init {
        head.type = SlotTypeEquipment(player, EntityEquipmentSlot.HEAD)
        chest.type = SlotTypeEquipment(player, EntityEquipmentSlot.CHEST)
        legs.type = SlotTypeEquipment(player, EntityEquipmentSlot.LEGS)
        feet.type = SlotTypeEquipment(player, EntityEquipmentSlot.FEET)
    }

}
