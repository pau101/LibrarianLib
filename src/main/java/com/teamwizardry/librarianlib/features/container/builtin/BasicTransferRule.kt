package com.teamwizardry.librarianlib.features.container.builtin

import com.teamwizardry.librarianlib.features.container.ITransferRule
import com.teamwizardry.librarianlib.features.container.SlotBase
import net.minecraft.item.ItemStack

/**
 * A basic transfer rule. It stores a list of source slots, target slots, and a filter.
 *
 * See [ITransferRule] for the behavior of transfer rules in containers
 *
 *
 */
open class BasicTransferRule : ITransferRule {
    protected val fromSet = mutableSetOf<SlotBase>()
    protected var filter: (SlotBase) -> Boolean = { true }
    protected val targets = mutableListOf<List<SlotBase>>()

    fun from(slots: List<SlotBase>): BasicTransferRule {
        fromSet.addAll(slots)
        return this
    }

    fun from(vararg slots: SlotBase): BasicTransferRule {
        fromSet.addAll(slots)
        return this
    }

    fun filter(filter: (SlotBase) -> Boolean): BasicTransferRule {
        this.filter = filter
        return this
    }

    fun to(slots: List<SlotBase>): BasicTransferRule {
        return to(*slots.toTypedArray())
    }

    fun to(vararg slots: SlotBase): BasicTransferRule {
        targets.add(listOf(*slots))
        return this
    }

    override fun shouldApply(slot: SlotBase): Boolean {
        return slot in fromSet && filter(slot)
    }

    override fun transferStack(stack: ItemStack): ItemStack? {
        var stack = stack
        for(target in targets) {
            val result = ITransferRule.mergeIntoRegion(stack, target.filter { it.visible })

            stack = result.remainingStack
            if(result.success || result.finished)
                return stack
        }
        return null // we only get here if nothing was successful or finished
    }
}
