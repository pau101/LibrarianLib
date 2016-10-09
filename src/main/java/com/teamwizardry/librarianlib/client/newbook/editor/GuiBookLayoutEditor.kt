package com.teamwizardry.librarianlib.client.newbook.editor

import com.teamwizardry.librarianlib.client.font.FontLoader
import com.teamwizardry.librarianlib.client.font.StringRenderer
import com.teamwizardry.librarianlib.client.gui.GuiBase
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.*
import com.teamwizardry.librarianlib.client.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.client.gui.mixin.DrawBorderMixin
import com.teamwizardry.librarianlib.client.gui.mixin.ResizableMixin
import com.teamwizardry.librarianlib.client.newbook.backend.Book
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
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

    val sidebar_uv: ComponentVoid
    val sidebar_u: ComponentTextInput
    val sidebar_v: ComponentTextInput

    val sidebar_xy: ComponentVoid
    val sidebar_x: ComponentTextInput
    val sidebar_y: ComponentTextInput
    val sidebar_z: ComponentTextInput

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


        //region Sidebar stuff
        //==============================================================================================================

        sidebar_uv = ComponentVoid(0, 50, 100, 10)
        sidebar_xy = ComponentVoid(0, 65, 100, 10)
        sidebar_z = ComponentTextInput(0, 80, 50, 10)
        sidebarInfo.add(ComponentRect(0, 80, 50, 10))

        sidebar_u = ComponentTextInput(0, 0, 50, 10)
        sidebar_v = ComponentTextInput(50, 0, 50, 10)

        sidebar_x = ComponentTextInput(0, 0, 50, 10)
        sidebar_y = ComponentTextInput(50, 0, 50, 10)

        val numregex = "-?\\d*".toRegex()
        val floatregex = "-?\\d*(?:\\.(?:\\d*)?)?".toRegex()

        sidebar_u.BUS.hook(ComponentTextInput.TextChangeEvent::class.java) { event ->
            if(!numregex.matches(event.newText))
                event.cancel()
        }
        sidebar_v.BUS.hook(ComponentTextInput.TextChangeEvent::class.java) { event ->
            if(!numregex.matches(event.newText))
                event.cancel()
        }
        sidebar_x.BUS.hook(ComponentTextInput.TextChangeEvent::class.java) { event ->
            if(!numregex.matches(event.newText))
                event.cancel()
            else {
                try {
                    val i = Integer.parseInt(event.newText)
                    selected?.let {
                        it.pos = it.pos.setX(i.toDouble())
                    }
                } catch (e: NumberFormatException) {}
            }
        }
        sidebar_y.BUS.hook(ComponentTextInput.TextChangeEvent::class.java) { event ->
            if(!numregex.matches(event.newText))
                event.cancel()
            else {
                try {
                    val i = Integer.parseInt(event.newText)
                    selected?.let {
                        it.pos = it.pos.setX(i.toDouble())
                    }
                } catch (e: NumberFormatException) {}
            }
        }
        sidebar_z.BUS.hook(ComponentTextInput.TextChangeEvent::class.java) { event ->
            if(!numregex.matches(event.newText))
                event.cancel()
            else {
                try {
                    val i = Integer.parseInt(event.newText)
                    selected?.let {
                        it.zIndex = i
                    }
                } catch (e: NumberFormatException) {}
            }
        }

        sidebar_uv.add(ComponentRect(0, 0, 100, 10))
        sidebar_uv.add(sidebar_u)
        sidebar_uv.add(sidebar_v)
        sidebarInfo.add(sidebar_uv)

        sidebar_xy.add(ComponentRect(0, 0, 100, 10))
        sidebar_xy.add(sidebar_x)
        sidebar_xy.add(sidebar_y)
        sidebarInfo.add(sidebar_xy)

        sidebarInfo.add(sidebar_z)

        //==============================================================================================================
        //endregion

        sprites.calculateOwnHover = false
        components.add(sprites)

        val componentsbg = ComponentVoid(-1000, -1000, 1000, 1000)
        componentsbg.zIndex = -1000
        componentsbg.BUS.hook(GuiComponent.MouseDownEvent::class.java) {
            if(componentsbg.mouseOver) select(null)
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

        select(rect)

//        val sr = StringRenderer()
//
//        var str = ""
//        for(i in 0x0000..0x04FF) {
//            str += String(Character.toChars(i))
//        }
//"§[ol]¡™£ §[ro]¡™£ §[rl]¡™£")//
//        sr.fontSize = 8
//        sr.addText("Here's some GNU Unifont and §{font=Arial}Here's some arial. §{style=bold}This is §{underline=pink}bold§{underline=off} §{style=italic}This is §{strike=F0F}italy\n\n§{strike=off}italic §{font=Unifont}Now we're back to §{style=plain}unifont, but §{shadow=on}with a §{7fff80bf}shadow§{ff80bf}, and now we're §{red}§{shadow=blue}opposite colo§{strike=orange}u§{strike=off}rs!")//"Hello world! I'm §4four-matted! Section §[mo]symbol§[r4nl]sign§[r4]! §rI'm not formatted! I'm §[ol]REALLY§r important.")//"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ac condimentum ex. Donec cursus rutrum tortor. Nullam tincidunt elit consectetur justo accumsan blandit. Cras porta mi nulla, rutrum hendrerit ex hendrerit in. Integer rutrum diam nec massa tincidunt maximus. Quisque ac diam purus. Integer consequat pretium augue, sed congue enim interdum in. Suspendisse pretium scelerisque nunc, a vulputate quam egestas eu. Vivamus hendrerit sodales aliquet. Nulla non libero in turpis laoreet aliquet non vel augue. Praesent pellentesque ante ut turpis hendrerit, et tempor nisl venenatis. Curabitur urna nunc, interdum at lectus ut, blandit consectetur dui. Nam varius rutrum mi, quis condimentum nulla convallis sed. In dapibus magna ut elit ornare, at ornare erat fringilla. Donec in mauris tincidunt nisi tincidunt sodales vel eget odio.")
//        sr.wrap = 200

//        sidebarInfo.BUS.hook(GuiComponent.PostDrawEvent::class.java) { event ->
//            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA)
//            sr.render(0, 30)
//            Minecraft.getMinecraft().fontRendererObj.unicodeFlag = true
//            Minecraft.getMinecraft().fontRendererObj.drawString("Hello world! I'm §4four-matted! Section §m§osymbol§r§4§n§lsign§r§4! §rI'm not formatted! I'm §o§lREALLY§r important.", 30, 80, 0)
//            Minecraft.getMinecraft().fontRendererObj.unicodeFlag = false
//        }

    }

    fun createRect(): GuiComponent<*> {
        val rect = ComponentRect(0, 0, 25, 25)
        val color = Color(ThreadLocalRandom.current().nextInt(0, 255), ThreadLocalRandom.current().nextInt(0, 255), ThreadLocalRandom.current().nextInt(0, 255))
        rect.color.func { color }
        ResizableMixin(rect, 8, false)
        DragMixin(rect, { it })

        rect.BUS.hook(GuiComponent.MouseDownEvent::class.java) { if(rect.mouseOver) select(rect) }

        sprites.add(rect)
        return rect
    }

    fun select(component: GuiComponent<*>?) {
        selected = component

    }

    fun resize(size: Vec2d) {
        sidebarInfo.size = sidebarInfo.size.setY(size.y)

        sidebarList.size = sidebarList.size.setY(size.y)
        sidebarList.pos = sidebarList.pos.setX(size.x - sidebarList.size.x)
    }

}