package com.teamwizardry.librarianlib.features.container

import net.minecraft.inventory.IInventory
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

/**
 * Easily manages slots and their types. Subclass this to make custom fields for specific slots or slot ranges.
 */
open class InventoryWrapper(val inventory: IItemHandler) {
    constructor(inv: IInventory) : this(InvWrapper(inv))

    private val slotArray = (0 until inventory.slots).map { SlotBase(inventory, it) }
    val all = slotArray
    val slots = SlotManager(slotArray)
    val types = TypeManager(slotArray)

    class SlotManager internal constructor(private val slotArray: List<SlotBase>) {

        operator fun get(range: IntRange): List<SlotBase> {
            return slotArray.subList(range.start, range.endInclusive + 1)
        }

        operator fun get(index: Int): SlotBase {
            return slotArray[index]
        }
    }

    class TypeManager internal constructor(private val slotArray: List<SlotBase>) {
        operator fun get(index: Int): SlotType {
            return slotArray[index].type
        }

        operator fun set(index: Int, type: SlotType) {
            slotArray[index].type = type
        }

        operator fun set(index: IntRange, type: SlotType) {
            for (i in index) {
                this[i] = type
            }
        }
    }
}
