package com.teamwizardry.librarianlib.client.gui

import java.util.function.Supplier

/**
 * Created by TheCodeWarrior
 */
class Value<T> (private var value: T) {

    var callback: Supplier<T>? = null

    /**
     * -- Kotlin function --
     *
     * Get the value
     */
    operator fun invoke(): T {
        val cb = callback
        if(cb == null) {
            return value
        } else {
            return cb.get()
        }
    }

    /**
     * -- Java funtion --
     *
     * Get the value
     */
    fun get(): T {
        return this()
    }

    /**
     * -- Kotlin function --
     *
     * Set the callback
     */
    operator fun invoke(cb: () -> T) {
        callback = Supplier<T>(cb)
    }

    /**
     * -- Kotlin function --
     *
     * Set the value
     */
    operator fun invoke(value: T) {
        this.value = value
        callback = null
    }

    /**
     * -- Java funtion --
     *
     * Set the callback
     */
    fun set(cb: Supplier<T>) {
        callback = cb
    }

    /**
     * -- Java function --
     *
     * Set the value
     */
    fun set(value: T) {
        this.value = value
        callback = null
    }

}