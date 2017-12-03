package com.teamwizardry.librarianlib.features.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack

open class SlotType {

    /**
     * Returned form the slot `isItemValid` method.
     */
    open fun isValid(slot: SlotBase, stack: ItemStack): Boolean {
        return true
    }

    /**
     * Returned from the slot `getSlotStackLimit` method.
     */
    open fun stackLimit(slot: SlotBase, stack: ItemStack): Int {
        return 64
    }

    /**
     * Returned from the slot `canTakeStack` method.
     */
    open fun canTake(slot: SlotBase, player: EntityPlayer?, stack: ItemStack): Boolean {
        return true
    }

    /**
     * try to shift click the item into this slot
     */
    open fun autoTransferInto(slot: SlotBase, stack: ItemStack): AutoTransferResult {
        if (!slot.isItemValid(stack))
            return AutoTransferResult(stack, false)
        val slotStack = slot.stack

        if (slotStack.isEmpty) {
            val quantity = Math.min(slot.slotStackLimit, stack.count)

            val leftOver = stack.copy()
            val insert = stack.copy()
            insert.count = quantity
            leftOver.count -= quantity

            slot.putStack(insert)
            return AutoTransferResult(if (leftOver.count <= 0) ItemStack.EMPTY else leftOver, true)
        }
        if (ITransferRule.areItemStacksEqual(stack, slotStack)) {
            val combinedSize = stack.count + slotStack.count
            val maxStackSize = Math.min(slot.getItemStackLimit(stack), stack.maxStackSize)

            if (combinedSize <= maxStackSize) {
                val newStack = slotStack.copy()
                newStack.count = combinedSize
                slot.putStack(newStack)

                return AutoTransferResult(ItemStack.EMPTY, true)
            } else {
                val leftoverStack = stack.copy()
                leftoverStack.count -= maxStackSize - slotStack.count

                val newStack = slotStack.copy()
                newStack.count = maxStackSize
                slot.putStack(newStack)

                return AutoTransferResult(leftoverStack, true)
            }
        }

        return AutoTransferResult(stack, false)
    }

    companion object {
        @JvmStatic
        val BASIC = SlotType()
    }

    /**
     * Handle a click on the slot, if the returned value is null normal handling will be called. If not null it will be
     * returned from the container slot click method. If the click was handled the default slot click
     * handling is not run.
     */
    open fun handleClick(slot: SlotBase, container: ContainerBase, dragType: Int, clickType: ClickType?, player: EntityPlayer): ItemStack? = null

    /**
     * Called in the slot `onPickupFromSlot` method. If this returns true the normal handling will proceed, false will
     * override the default handling causing it not to be called.
     */
    open fun onTake(slot: SlotBase, player: EntityPlayer, stack: ItemStack) = true

    /**
     * Called when the slot changes.
     */
    open fun onSlotChange(slot: SlotBase) {}

    /**
     * Called when setting the stack in [slot] to [stack]. Return true to disable default behavior.
     */
    open fun setStack(slot: SlotBase, stack: ItemStack) = true


    /**
     * Returned from the slot `getStack` method. It is passed the default stack, and may modify or entirely replace the
     * return value
     */
    open fun getStack(slot: SlotBase, defaultStack: ItemStack) = defaultStack

    /**
     * Returned from the slot `canBeHovered` method.
     */
    open fun canHover(slot: SlotBase) = true

}

