package com.teamwizardry.librarianlib.features.container

import com.teamwizardry.librarianlib.features.container.builtin.BasicTransferRule
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
abstract class ContainerBase(val player: EntityPlayer) : Container() {

    fun addSlots(wrapper: InventoryWrapper) {
        wrapper.all.forEach {
            addSlotToContainer(it)
        }
    }

    /**
     * Create a new basic transfer rule
     */
    fun transferRule(): BasicTransferRule {
        val rule = BasicTransferRule()
        transferRules.add(rule)
        return rule
    }

    val transferRules = mutableListOf<ITransferRule>()

    //region Implementation details
    @Suppress("UNCHECKED_CAST")
    internal val slots = inventorySlots as List<SlotBase>
    override fun canInteractWith(playerIn: EntityPlayer?): Boolean {
        return true
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType?, player: EntityPlayer): ItemStack {
        if (slotId > 0 && slotId < inventorySlots.size) {
            val slot = inventorySlots[slotId] as SlotBase

            val pair = slot.handleClick(this, dragType, clickTypeIn, player)
            if (pair != null) {
                return pair
            }
        }

        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? {
        val slot = inventorySlots[index] as SlotBase
        val stack = slot.stack
        if (stack.isEmpty) return stack
        for (rule in transferRules) {
            if (rule.shouldApply(slot)) {
                val result = rule.transferStack(stack)
                if (result != null) {
                    slot.putStack(result)
                    return ItemStack.EMPTY
                }
            }
        }
        return ItemStack.EMPTY
    }

    override public fun addSlotToContainer(slotIn: Slot?): Slot {
        if(slotIn !is SlotBase) {
            throw IllegalArgumentException("LibrarianLib containers only support subclasses of `SlotBase`")
        }
        return super.addSlotToContainer(slotIn)
    }
    //endregion
}
