package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.component.HookPriority
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.lang.reflect.Method
import java.util.regex.Pattern

internal class ComponentEventHookMethodHandler(val holder: Any, val component: GuiComponent) {
    constructor(component: GuiComponent) : this(component, component)

    init {
        if(holder != component) {
            component.eventHookMethodHandler.auxiluaryHandlers.add(this)
        }
    }
    val auxiluaryHandlers = mutableListOf<ComponentEventHookMethodHandler>()
    val methods = cache[holder.javaClass]

    init {
        methods.selfEvents.forEach { key, value ->
            @Suppress("UNCHECKED_CAST")
            component.BUS.hook(key as Class<Event>) { event: Event ->
                value.forEach {
                    it.hook(holder, event)
                }
            }
        }
        component.BUS.postEventHook = { event: Event ->
            if(component.name != "") {
                this.ripple(event, component.name)
            }
        }
    }

    fun ripple(event: Event, name: String) {
        methods.namedEvents[event.javaClass]?.forEach {
            if(it.matches(name)) {
                it.hook(holder, event)
            }
        }
        component.parent?.eventHookMethodHandler?.ripple(event, name)
        auxiluaryHandlers.forEach { it.ripple(event, name) }
    }

   companion object {
        val cache = mutableMapOf<Class<*>, EventCache>().withRealDefault { EventCache(it) }

        class EventCache(clazz: Class<*>) {

            val selfEvents: Map<Class<*>, List<DataEventHook>>
            val namedEvents: Map<Class<*>, List<DataEventHook>>

            init {
                val methods = mutableListOf<Method>()

                var cls: Class<*>? = clazz
                while (cls != null) {
                    methods.addAll(cls.declaredMethods)
                    cls = cls.superclass
                }

                val events = methods
                        .filter { it.isAnnotationPresent(Hook::class.java) }
                        .filter { it.parameterCount == 1 && Event::class.java.isAssignableFrom(it.parameterTypes[0]) }
                        .map {
                            it.isAccessible = true
                            val annot = it.getAnnotation(Hook::class.java)

                            val mh = MethodHandleHelper.wrapperForMethod<Any>(it)
                            @Suppress("UNCHECKED_CAST")
                            return@map DataEventHook(
                                    it.parameterTypes[0],
                                    annot.value,
                                    annot.priority,
                                    { comp: Any, event: Event -> mh(comp, arrayOf(event)); Unit }
                            )
                        }

                val selfEvents = mutableMapOf<Class<*>, MutableList<DataEventHook>>()
                val namedEvents = mutableMapOf<Class<*>, MutableList<DataEventHook>>()
                events.forEach { data ->
                    if(data.name == "") {
                        selfEvents.getOrPut(data.event, { mutableListOf() }).add(data)
                    } else {
                        namedEvents.getOrPut(data.event, { mutableListOf() }).add(data)
                    }
                }
                this.selfEvents = selfEvents.mapValues { (_, value) -> value.sortedBy { it.priority } }
                this.namedEvents = namedEvents.mapValues { (_, value) -> value.sortedBy { it.priority } }
            }
        }

        data class DataEventHook(val event: Class<*>, val name: String, val priority: HookPriority, val hook: (Any, Event) -> Unit) {
            val regex = if(name.startsWith("/") && name.endsWith("/")) {
                Regex(name.substring(1 until name.length-1))
            } else {
                null
            }

            fun matches(name: String): Boolean {
                if(regex == null)
                    return name == this.name
                else
                    return regex.matches(name)
            }
        }
    }
}
