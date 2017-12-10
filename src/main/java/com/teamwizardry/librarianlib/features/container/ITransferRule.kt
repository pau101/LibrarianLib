package com.teamwizardry.librarianlib.features.container

import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import net.minecraft.item.ItemStack

/**
 * A single handler for defining how and where items should be shift-clicked
 *
 * When a slot is shift clicked the container will find the first applicable transfer rule and attempt to transfer the
 * item using that rule. If the rule successfully inserted any of that item into a slot, it will stop. Otherwise it will
 * attempt the next transfer rule. At the end it will set the slot's stack to what remains of the stack after transferring.
 *
 * @see AutoTransferResult
 */
interface ITransferRule {
    /**
     * Return true if the associated targets should be applied for the passed slot
     */
    fun shouldApply(slot: SlotBase): Boolean

    /**
     * Transfer stack from [stack] using this rule. Return the remaining items if the stack was successfully inserted or
     * null if it was not
     */
    fun transferStack(stack: ItemStack): ItemStack?

    companion object {

        /**
         * Attempt to merge the [stack] into the [region].
         */
        fun mergeIntoRegion(stack: ItemStack, region: List<SlotBase>): AutoTransferResult {
            var result = AutoTransferResult(stack.copy(), false, false)
            var anySuccess = false

            if(stack.isStackable) { // no sense trying to prioritize slots with similar items if the items can't stack

                // try to merge stack into slots that already have items (don't fill up empty slots unless we need to)
                for(slot in region) {
                    if (areItemStacksEqual(stack, slot.stack)) {
                        result = slot.type.autoTransferInto(slot, result.remainingStack)
                        anySuccess = anySuccess || result.success
                        if (result.finished) break
                    }
                }
            }

            if (!result.finished && result.remainingStack.isNotEmpty) {
                for(slot in region) {
                    result = slot.type.autoTransferInto(slot, result.remainingStack)
                    anySuccess = anySuccess || result.success

                    if(result.finished) break
                }
            }

            return AutoTransferResult(result.remainingStack, anySuccess, result.finished)
        }


        fun areItemStacksEqual(stackA: ItemStack, stackB: ItemStack): Boolean {
            return (stackA.isEmpty && stackB.isEmpty) ||
                    (stackB.item === stackA.item &&
                            stackA.itemDamage == stackB.itemDamage &&
                            ItemStack.areItemStackTagsEqual(stackA, stackB) &&
                            stackA.areCapsCompatible(stackB)
                            )
        }

    }

}

data class AutoTransferResult(
        /**
         * The stack remaining after the transfer
         */
        val remainingStack: ItemStack,
        /**
         * Whether any of the item was successfully inserted
         */
        val success: Boolean,
        /**
         * Whether the Auto Transfer system should stop attempting to insert the item despite it being non-empty
         */
        val finished: Boolean = remainingStack.isEmpty
)
