package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.lang.reflect.Method

internal class ComponentEventHookMethodHandler(val component: GuiComponent) {

    val methods = cache[component.javaClass]

    init {
        methods.selfEvents.forEach {
            @Suppress("UNCHECKED_CAST")
            component.BUS.hook(it.key as Class<Event>) { event: Event ->
                it.value(component, event)
            }
        }
        component.BUS.postEventHook = { event: Event ->
            if(component.name != "") {
                this.ripple(event, component.name)
            }
        }
    }

    fun ripple(event: Event, name: String) {
        methods.namedEvents[event.javaClass to name]?.invoke(component, event)
        component.parent?.eventHookMethodHandler?.ripple(event, name)
    }

    companion object {
        val cache = mutableMapOf<Class<*>, EventCache>().withRealDefault { EventCache(it) }

        class EventCache(clazz: Class<*>) {
            val selfEvents: Map<Class<*>, (Any, Event) -> Unit>
            val namedEvents: Map<Pair<Class<*>, String>, (Any, Event) -> Unit>

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
                        .associate {
                            it.isAccessible = true
                            val annot = it.getAnnotation(Hook::class.java)

                            val mh = MethodHandleHelper.wrapperForMethod<Any>(it)
                            @Suppress("UNCHECKED_CAST")
                            return@associate (it.parameterTypes[0] to annot.value) to { comp: Any, event: Event -> mh(comp, arrayOf(event)); Unit }
                        }

                selfEvents = events.filter { it.key.second == "" }.mapKeys { it.key.first }
                namedEvents = events.filter { it.key.second != "" }
            }
        }
    }
}
