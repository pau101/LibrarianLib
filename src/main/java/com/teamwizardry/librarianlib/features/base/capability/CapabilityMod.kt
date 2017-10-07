package com.teamwizardry.librarianlib.features.base.capability

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.event.AttachCapabilitiesEvent
import java.util.*

/**
 * Created by Elad on 10/7/2017.
 */
@SaveInPlace
abstract class CapabilityMod<T : Any> {
    fun attach(event: AttachCapabilitiesEvent<*>) {
        event.addCapability(ResourceLocation("librarianlib", realName), Serializer())
    }

    abstract val clazz: Class<T>
    private val constructor by lazy { MethodHandleHelper.wrapperForConstructor<Any>(clazz) }
    private val realName by lazy { clazz.name.intern() }
    private val capability by lazy {
        (delegate(CapabilityManager.INSTANCE) as IdentityHashMap<String, Capability<*>>)[realName]!! as Capability<T>
    }
    init {
        capabilityMods.add(this)
    }
    companion object {
        private val delegate = MethodHandleHelper.wrapperForGetter<CapabilityManager>(CapabilityManager::class.java.getDeclaredField("providers"))
        private val capabilityMods = mutableListOf<CapabilityMod<*>>()
        internal fun registerCapabilities() = capabilityMods.forEach {
            CapabilityManager.INSTANCE.register(it.clazz as Class<Any>, it.Storage() as Capability.IStorage<Any>) { it.constructor(arrayOf()) }
        }
    }
    open fun writeCustomNBT(nbtTagCompound: NBTTagCompound, t: T) {
        // NO-OP
    }

    open fun readCustomNBT(nbtTagCompound: NBTTagCompound, t: T) {
        // NO-OP
    }

    fun writeToNBT(compound: NBTTagCompound, t: T): NBTTagCompound {
        compound.setTag("auto", AbstractSaveHandler.writeAutoNBT(t, false))
        writeCustomNBT(compound, t)
        return compound
    }

    fun readFromNBT(compound: NBTTagCompound, t: T) {
        AbstractSaveHandler.readAutoNBT(t, compound.getTag("auto"), false)
        readCustomNBT(compound, t)
    }

    protected open inner class Storage : Capability.IStorage<T> {
        override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? {
            return writeToNBT(NBTTagCompound(), instance)
        }
        override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase) {
            readFromNBT(nbt as NBTTagCompound, instance)
        }
    }
    protected open inner class Serializer : ICapabilitySerializable<NBTTagCompound> {
        override fun serializeNBT(): NBTTagCompound {
            val tag = NBTTagCompound()
            tag.setTag("save", AbstractSaveHandler.writeAutoNBT(capability.defaultInstance!!, true))
            return tag
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            return if (capability == this@CapabilityMod.capability) this@CapabilityMod as T else null
        }

        override fun deserializeNBT(nbt: NBTTagCompound) {
            AbstractSaveHandler.readAutoNBT(capability.defaultInstance!!, nbt, true)
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability == this@CapabilityMod.capability
        }

    }
}
