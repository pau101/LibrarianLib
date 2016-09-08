package com.teamwizardry.librarianlib.client.newbook.editor

import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentCenterAlign
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.client.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.client.gui.mixin.DrawBorderMixin
import com.teamwizardry.librarianlib.client.gui.mixin.ResizableMixin
import com.teamwizardry.librarianlib.client.newbook.backend.Book
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiBookLayoutEditor(val book: Book) : GuiBase(0, 0) {

    companion object {
        val TEX = Texture(ResourceLocation("librarianlib:textures/gui/editorUI.png"))
    }

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
    val sidebarList = ComponentRect(0, 0, 100, 0)
    val sidebarInfo = ComponentRect(0, 0, 100, 0)

    init {

        val center = ComponentCenterAlign(0,0,true,true)
        center.add(mainComponent)
        DrawBorderMixin(mainComponent).color.setValue(Color.MAGENTA)

        sidebarList.color.setValue(Color.LIGHT_GRAY)
        sidebarInfo.color.setValue(Color.LIGHT_GRAY)
        fullscreenComponents.BUS.hook(GuiComponent.SetSizeEvent::class.java) {
            resize(it.size)
        }

        components.add(center)
        fullscreenComponents.add(sidebarList)
        fullscreenComponents.add(sidebarInfo)

        var rect = ComponentRect(0, 0, 25, 25)
        rect.color.func { Color.CYAN }
        ResizableMixin(rect, 8, false)
        DragMixin(rect, { it })
        components.add(rect);

    }

    fun resize(size: Vec2d) {
        sidebarInfo.size = sidebarInfo.size.setY(size.y)

        sidebarList.size = sidebarList.size.setY(size.y)
        sidebarList.pos = sidebarList.pos.setX(size.x - sidebarList.size.x)
    }

}