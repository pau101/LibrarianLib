package com.teamwizardry.librarianlib.client.gui.mixin

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.Vec2d

class DragMixin<T : GuiComponent<T>>(protected var component: T, protected var constraints: (Vec2d) -> Vec2d) {

    class DragPickupEvent<out T : GuiComponent<*>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class DragDropEvent<out T : GuiComponent<*>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class DragMoveEvent<out T : GuiComponent<*>>(val component: T, val mousePos: Vec2d, val clickedPoint: Vec2d, val pos: Vec2d, var newPos: Vec2d, val button: EnumMouseButton) : Event()

    var mouseDown: EnumMouseButton? = null
    var clickPos = Vec2d.ZERO
    var autoOffsetPos = Vec2d.ZERO

    init {
        init()
    }

    private fun init() {
        component.BUS.hook(GuiComponent.MouseDownEvent::class.java) { event ->
            if (mouseDown == null && event.component.mouseOver) {
                val e = component.BUS.fire(DragPickupEvent(event.component, event.mousePos, event.button))
                if (!e.isCanceled()) {
                    mouseDown = event.button
                    clickPos = event.mousePos.add(autoOffsetPos)
                    event.cancel()
                }
            }
        }

        component.BUS.hook(GuiComponent.MouseUpEvent::class.java) { event ->
            if (mouseDown == event.button) {
                val e = component.BUS.fire(DragDropEvent(event.component, event.mousePos, event.button))
                if (!e.isCanceled()) {
                    mouseDown = null
                    event.cancel()
                }
            }
        }

        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) { event ->
            val mouseButton = mouseDown
            if (mouseButton != null) {
                val newPos = constraints(event.component.pos.add(event.mousePos).sub(clickPos).add(autoOffsetPos))

                if (newPos != event.component.pos) {
                    event.component.pos = event.component.BUS.fire(
                            DragMoveEvent(event.component, event.mousePos, clickPos, event.component.pos, newPos, mouseButton)
                    ).newPos
                }
            }
        }
    }
}
