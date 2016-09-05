package com.teamwizardry.librarianlib.client.newbook.editor

import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentCenterAlign
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.client.gui.mixin.DrawBorderMixin
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiBookLayoutEditor : GuiBase(0, 0) {

    var bookWidth = 100
        set(value) {
            field = value
            mainComponent.size = mainComponent.size.setX(value.toDouble())
        }
    var bookHeight = 150
        set(value) {
            field = value
            mainComponent.size = mainComponent.size.setY(value.toDouble())
        }

    val mainComponent = ComponentVoid(0,0, bookWidth, bookHeight)
    val sidebar = ComponentRect(0, 0, 100, 0)

    init {

        val center = ComponentCenterAlign(0,0,true,true)
        center.add(mainComponent)
        DrawBorderMixin(mainComponent).color.setValue(Color.MAGENTA)

        sidebar.color.setValue(Color.LIGHT_GRAY)
        fullscreenComponents.BUS.hook(GuiComponent.SetSizeEvent::class.java) {
            resize(it.size)
        }

        components.add(center)
        fullscreenComponents.add(sidebar)
    }

    fun resize(size: Vec2d) {
        sidebar.size = sidebar.size.setY(size.y)
        sidebar.pos = sidebar.pos.setX(size.x - sidebar.size.x)
    }

}