package com.teamwizardry.librarianlib.client.gui.mixin

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.plus

/**
 * Created by TheCodeWarrior
 */
class ResizableMixin<T: GuiComponent<T>> constructor(component: T, edgeSize: Int, allSides: Boolean) {

    class ChangeSizeEvent<T : GuiComponent<*>>(val component: T, var newPos: Vec2d, var newSize: Vec2d) : EventCancelable()


    var mouseDown: EnumMouseButton? = null
    var clickPos = Vec2d.ZERO
    var sideClicked = Vec2d.ZERO

    init {
        component.BUS.hook(GuiComponent.MouseDownEvent::class.java) { event ->
            if(event.isCanceled()) return@hook
            if (mouseDown == null && component.mouseOver && !component.BUS.fire(DragMixin.DragPickupEvent(event.component, event.mousePos, event.button)).isCanceled()) {
                var action = false
                if(!allSides) {
                    val cornerDist = component.size - event.mousePos
                    if(cornerDist.x >= 0 && cornerDist.y >= 0 && cornerDist.x + cornerDist.y < edgeSize) {
                        action = true
                        sideClicked = Vec2d.ONE
                        clickPos = event.mousePos - component.size
                    }
                } else {
                    var x = 0
                    var y = 0
                    var cpX = 0.0
                    var cpY = 0.0
                    if (event.mousePos.x < edgeSize) {
                        x = -1
                        cpX = event.mousePos.x
                    }
                    if (event.mousePos.y < edgeSize) {
                        y = -1
                        cpY = event.mousePos.y
                    }
                    if (event.mousePos.x > component.size.x - edgeSize) {
                        x = 1
                        cpX = component.size.x - event.mousePos.x
                    }
                    if (event.mousePos.y < component.size.y - edgeSize) {
                        y = 1
                        cpY = component.size.y - event.mousePos.y
                    }

                    sideClicked = Vec2d(x, y)
                    clickPos = Vec2d(cpX, cpY)
                    if (x != 0 || y != 0)
                        action = true
                }
                if(action) {
                    mouseDown = event.button
                    event.cancel()
                }
            }
        }
        component.BUS.hook(GuiComponent.MouseUpEvent::class.java) { event ->
            if(event.isCanceled()) return@hook
            if (mouseDown == event.button && !component.BUS.fire(DragMixin.DragDropEvent(event.component, event.mousePos, event.button)).isCanceled()) {
                mouseDown = null
                sideClicked = Vec2d.ZERO
                event.cancel()
            }
        }
        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) { drawEvent ->
            val mouseButton = mouseDown
            if (mouseButton != null) {
                val diff = drawEvent.mousePos - clickPos
                var pos =  component.pos
                var size = component.size

                if(sideClicked.x > 0) {
                    size = size.setX( drawEvent.mousePos.x - clickPos.x )
                }
                if(sideClicked.y > 0) {
                    size = size.setY( drawEvent.mousePos.y - clickPos.y )
                }

                if(sideClicked.x < 0) {
                    pos = pos.setX( component.pos.x + drawEvent.mousePos.x - clickPos.x )
                }
                if(sideClicked.y < 0) {
                    pos = pos.setY( component.pos.y + drawEvent.mousePos.y - clickPos.y )
                }

                val event = ChangeSizeEvent(component, pos, size)
                component.BUS.fire(event)

                if(!event.isCanceled()) {
                    if(event.newPos != component.pos) {
                        component.pos = event.newPos
                    }
                    if(event.newSize != component.size) {
                        component.size = event.newSize
                    }
                }
            }
        }
    }
}