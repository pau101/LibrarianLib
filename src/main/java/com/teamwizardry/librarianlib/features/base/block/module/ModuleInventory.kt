package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.base.block.TileMod
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleInventory(handler: ItemStackHandler) : ModuleCapability<ItemStackHandler>(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, handler) {
    constructor() : this(ItemStackHandler())
    constructor(size: Int) : this(ItemStackHandler(size))
    constructor(stacks: NonNullList<ItemStack>) : this(ItemStackHandler(stacks))

    override fun onBreak(tile: TileMod) {
        (0 until handler.slots)
                .map { handler.getStackInSlot(it) }
                .filterNot { it.isEmpty }
                .forEach { InventoryHelper.spawnItemStack(tile.world, tile.pos.x.toDouble(), tile.pos.y.toDouble(), tile.pos.z.toDouble(), it) }
    }
}
