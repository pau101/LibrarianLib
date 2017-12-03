package com.teamwizardry.librarianlib.features.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by TheCodeWarrior
 */
class SlotBase(handler: IItemHandler, index: Int) : SlotItemHandler(handler, index, 0, 0) {
    var type = SlotType.BASIC
    var visible = true
    var lastVisible = visible

    /**
     * Directly set the slot's stack to [stack]
     */
    fun set(stack: ItemStack) {
        super.putStack(stack)
    }

    override fun onTake(thePlayer: EntityPlayer, stack: ItemStack): ItemStack {
        if (type.onTake(this, thePlayer, stack))
            return super.onTake(thePlayer, stack)
        return ItemStack.EMPTY
    }

    override fun onSlotChanged() {
        type.onSlotChange(this)
        super.onSlotChanged()
    }

    override fun putStack(stack: ItemStack) {
        if (type.setStack(this, stack))
            super.putStack(stack)
    }

    override fun getStack(): ItemStack {
        return if (visible) type.getStack(this, super.getStack()) else ItemStack.EMPTY
    }

    override fun canTakeStack(playerIn: EntityPlayer): Boolean {
        return if (visible) super.canTakeStack(playerIn) && type.canTake(this, playerIn, stack) else false
    }

    @SideOnly(Side.CLIENT)
    override fun isEnabled(): Boolean {
        return if (visible) type.canHover(this) else false
    }

    override fun isItemValid(stack: ItemStack): Boolean {
        return if (visible) super.isItemValid(stack) && type.isValid(this, stack) else false
    }

    override fun getSlotStackLimit(): Int {
        return type.stackLimit(this, stack)
    }

    fun handleClick(container: ContainerBase, dragType: Int, clickType: ClickType?, player: EntityPlayer): ItemStack? {
        return type.handleClick(this, container, dragType, clickType, player)
    }
}
