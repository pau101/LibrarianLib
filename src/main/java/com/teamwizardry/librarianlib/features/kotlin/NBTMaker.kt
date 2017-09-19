@file:JvmName("NBTMaker")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.utilities.setObject
import net.minecraft.nbt.*
import net.minecraft.util.ResourceLocation
import java.util.*

/**
 * @author WireSegal
 * Created at 3:36 PM on 10/20/16.
 * and elad fixed the shit out of it to make it cool
 */

fun <T> list(vararg args: T): NBTTagList {
    val list = NBTTagList()
    args.forEach { list.appendTag(convertNBT(it)) }
    return list
}

fun comp(vararg args: Pair<String, *>): NBTTagCompound {
    val comp = NBTTagCompound()
    args.forEach { comp.setTag(it.first, convertNBT(it.second)) }
    return comp
}


fun convertNBT(value: Any?): NBTBase = when (value) {
    is NBTBase -> value

    is Boolean -> NBTTagByte(if (value) 1 else 0)
    is Byte -> NBTTagByte(value)
    is Char -> NBTTagShort(value.toShort())
    is Short -> NBTTagShort(value)
    is Int -> NBTTagInt(value)
    is Long -> NBTTagLong(value)
    is Float -> NBTTagFloat(value)
    is Double -> NBTTagDouble(value)
    is ByteArray -> NBTTagByteArray(value)
    is String -> NBTTagString(value)
    is IntArray -> NBTTagIntArray(value)
    is UUID -> NBTTagList().apply {
        appendTag(NBTTagLong(value.leastSignificantBits))
        appendTag(NBTTagLong(value.mostSignificantBits))
    }
    is Array<*> -> list(*value)
    is Collection<*> -> list(*value.toTypedArray())
    is Map<*, *> -> comp(*value.toList().map { it.first.toString() to it.second }.toTypedArray())
    is ResourceLocation -> NBTTagString(value.toString())
    is Pair<*, *> -> comp(value as Pair<String, *>)
    else -> throw IllegalArgumentException("Unrecognized type: " + value)
}

// Not inline because hot reloading fails on inline obfuscated classes under some circumstances
fun nbt(lambda: Nbt.() -> Nbt) = Nbt().lambda().process()

fun nbtCompoound(lambda: NbtCompound.() -> NBTTagCompound) = NbtCompound().lambda()

fun nbtList(lambda: NbtList.() -> NBTTagList) = NbtList().lambda()

class Nbt {
    val inputs = mutableListOf<Any>()
    operator fun Any.unaryPlus(): Nbt {
        inputs.add(this)
        return this@Nbt
    }

    // fun for the whole family!
    fun process(): NBTBase {
        return when {
            inputs.size == 1 -> convertNBT(inputs[0])
            inputs.isEmpty() -> NBTTagCompound()
            inputs.all { it is Pair<*, *> } -> comp(*inputs.map { it as Pair<String, *> }.toTypedArray())
            else -> list(*inputs.toTypedArray())
        }
    }
}

class NbtCompound : NBTTagCompound() {
    operator fun Pair<String, Any>.unaryPlus(): NbtCompound {
        this@NbtCompound.setObject(first, convertNBT(second))
        return this@NbtCompound
    }
}

class NbtList : NBTTagList() {
    operator fun Any.unaryPlus(): NbtList {
        appendTag(convertNBT(this))
        return this@NbtList
    }
}


fun main(x: Array<String>) {
    println(nbt {
        +("tag1" to "tag2")
        +("tag2" to nbt {
            +"5"
            +"6"
        })
    })
}

