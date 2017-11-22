package com.teamwizardry.librarianlib.features.gui

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Allows a value to be set as either the result of a callback or a static value
 */
class CallbackValue<T>(private var value: T) {
    private var callback: (() -> T)? = null

    /**
     * Set the callback and override the current fixed value
     */
    fun set(callback: () -> T) {
        this.callback = callback
    }

    /**
     * Kotlin shortcut for [set]
     */
    operator fun invoke(callback: () -> T) {
        this.callback = callback
    }

    inner class Delegate<R> : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>): T {
            callback?.also {
                return@getValue it()
            }
            return value
        }

        override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            this@CallbackValue.value = value
            callback = null
        }

    }
}