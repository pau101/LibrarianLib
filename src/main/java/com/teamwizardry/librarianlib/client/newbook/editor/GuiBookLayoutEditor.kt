package com.teamwizardry.librarianlib.client.newbook.editor

import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentCenterAlign
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import com.teamwizardry.librarianlib.client.gui.components.ComponentText
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.client.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.client.gui.mixin.DrawBorderMixin
import com.teamwizardry.librarianlib.client.gui.mixin.ResizableMixin
import com.teamwizardry.librarianlib.client.newbook.backend.Book
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class GuiBookLayoutEditor(val book: Book) : GuiBase(0, 0) {

    var selected: GuiComponent<*>? = null

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
    val sprites = ComponentVoid(0, 0, 0, 0)

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

        val pos = ComponentText(1, 1, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        val size = ComponentText(1, 10, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)

        pos.text.func {
            val sel = selected
            if(sel == null)
                "(N/A, N/A)"
            else
                "(${sel.pos.xi}, ${sel.pos.yi})"
        }

        size.text.func {
            val sel = selected
            if(sel == null)
                "N/A x N/A"
            else
                "${sel.size.xi} x ${sel.size.yi}"
        }

        sidebarInfo.add(pos)
        sidebarInfo.add(size)

        sprites.calculateOwnHover = false
        components.add(sprites)

        val componentsbg = ComponentVoid(-1000, -1000, 1000, 1000)
        componentsbg.zIndex = -1000
        componentsbg.BUS.hook(GuiComponent.MouseDownEvent::class.java) {
            if(componentsbg.mouseOver) selected = null
        }
        sprites.add(componentsbg)

        val rect = createRect()
        createRect()
        createRect()
        createRect()
        createRect()
        createRect()
        createRect()
        createRect()

        selected = rect


    }

    fun createRect(): GuiComponent<*> {
        val rect = ComponentRect(0, 0, 25, 25)
        val color = Color(ThreadLocalRandom.current().nextInt(0, 255), ThreadLocalRandom.current().nextInt(0, 255), ThreadLocalRandom.current().nextInt(0, 255))
        rect.color.func { color }
        ResizableMixin(rect, 8, false)
        DragMixin(rect, { it })

        rect.BUS.hook(GuiComponent.MouseDownEvent::class.java) { if(rect.mouseOver) selected = rect }

        sprites.add(rect)
        return rect
    }

    fun resize(size: Vec2d) {
        sidebarInfo.size = sidebarInfo.size.setY(size.y)

        sidebarList.size = sidebarList.size.setY(size.y)
        sidebarList.pos = sidebarList.pos.setX(size.x - sidebarList.size.x)
    }

}