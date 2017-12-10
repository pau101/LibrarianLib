package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.InventoryWrapperPlayer
import com.teamwizardry.librarianlib.features.container.builtin.SlotTypeGhost
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.GuiContainerBase
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.component.named
import com.teamwizardry.librarianlib.features.gui.component.supporting.LayoutConstantCell
import com.teamwizardry.librarianlib.features.gui.pastry.*
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ContainerTest(player: EntityPlayer, tile: TEContainer) : ContainerBase(player) {

    val invPlayer = InventoryWrapperPlayer(player)
    val invBlock = TestWrapper(tile)

    init {
        addSlots(invPlayer)
        addSlots(invBlock)

        transferRule().from(invPlayer.main).from(invPlayer.hotbar).to(invPlayer.head).filter {
            it.stack.item == Items.DIAMOND_HELMET
        }

        transferRule().from(invPlayer.main).to(invBlock.main)
        transferRule().from(invPlayer.main).to(invBlock.small)
        transferRule().from(invPlayer.hotbar).to(invBlock.small)
        transferRule().from(invPlayer.hotbar).to(invBlock.main)
        transferRule().from(invBlock.main).to(invPlayer.main)
        transferRule().from(invBlock.small).to(invPlayer.hotbar)
    }

    companion object {
        val NAME = ResourceLocation("librarianlibtest:container")

        init {
            GuiHandler.registerTileContainer(NAME, { player, _, tile -> ContainerTest(player, tile as TEContainer) }, { _, container -> GuiContainerTest(container) })
        }
    }
}

class TestWrapper(te: TEContainer) : InventoryWrapper(te) {
    val main = slots[0..26]
    val small = slots[27..35]

    init {
        small.forEach { it.type = SlotTypeGhost(32, true) }
    }
}

class GuiContainerTest(container: ContainerTest) : GuiContainerBase(container, 197, 166) {

    val offhandLeft = LayoutConstantCell(4)
    val upperGrid = PastryInventoryGrid(container.invBlock.main, 9)
    val smallGrid = PastryInventoryGrid(container.invBlock.small, 3)

    @Hook("moveButton")
    fun moveClick(e: GuiComponentEvents.MouseClickEvent) {
        smallGrid.isVisible = !smallGrid.isVisible
    }

    init {
        val bg = PastryBackground()

        mainComponents.add(bg)

        val offhand = PastrySlot(container.invPlayer.offhand)
        val hotbar = PastryInventoryRow(container.invPlayer.hotbar)
        val main = PastryInventoryGrid(container.invPlayer.main, 9)
        val armor = PastryInventoryColumn(container.invPlayer.armor)

        val button = PastryButton("Move").named("moveButton")


        mainComponents.add(offhand, main, hotbar, armor, upperGrid, smallGrid, button)
        layout {
            bg.layout.boundsEqualTo(mainComponents)

            armor.layout.left eq mainComponents.layout.left + 4
            armor.layout.top eq mainComponents.layout.top + 4

            offhand.layout.bottom eq bg.layout.top + 4
            offhand.layout.left eq mainComponents.layout.left + offhandLeft

            hotbar.layout.bottom eq mainComponents.layout.bottom - 4
            hotbar.layout.centerX eq mainComponents.layout.centerX

            main.layout.bottom eq hotbar.layout.top - 4
            main.layout.centerX eq mainComponents.layout.centerX

            button.layout.right eq mainComponents.layout.right - 2
            button.layout.top eq mainComponents.layout.top + 2

            upperGrid.layout.left eq armor.layout.right + 4
            upperGrid.layout.top eq mainComponents.layout.top + 4

            smallGrid.layout.left eq bg.layout.right + 4
            smallGrid.layout.top eq mainComponents.layout.top + 4
        }

        val anim = BasicAnimation(offhandLeft, "value")
        anim.from = 4
        anim.to = 150
        anim.duration = 80f
        anim.easing = Easing.easeInOutSine
        anim.repeatCount = -1
        anim.shouldReverse = true
        offhand.add(anim)
        offhand.geometry.integerBounds = true

//        val layout = PastryInventoryPlayer(container.invPlayer)
//        b.add(layout.inventory)
//
//        layout.armor.pos = vec(6, 12)
//        layout.armor.isVisible = true
//        layout.offhand.pos = vec(6, 84)
//        layout.offhand.isVisible = true
//        layout.inventory.pos = vec(29, 84)
//
//        val grid = PastryInventoryGrid(container.invBlock.main, 9)
//        grid.pos = vec(29, 12)
//        b.add(grid)
//
//        val s = ComponentSprite(slider, 197, 79)
//        s.isVisible = false
//        b.add(s)
//
//        val miniGrid = PastryInventoryGrid(container.invBlock.small, 3)
//        miniGrid.pos = vec(3, 5)
//        s.add(miniGrid)
//
//        val button = ComponentRect(178, 68, 12, 11)
//        button.color = Color(0, 0, 0, 127)
//
//        button.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
//            s.isVisible = !s.isVisible
//        }
//
//        b.add(button)
//        // CIRCLE!!!
//        /*
//        grid.rows[2].pos += vec(18*4.5, 40)
//
//        var a = 0.0
//        val aFrame = (2*Math.PI)/360
//        val aPer = (2*Math.PI)/9
//        val radius = 30
//
//        val row = grid.slots[2]
//
//        grid.root.BUS.hook(GuiComponent.ComponentTickEvent::class.java) {
//            a += aFrame
//
//            row.forEachIndexed { i, slot ->
//                val s = Math.sin(a + aPer*i)
//                val c = Math.cos(a + aPer*i)
//                slot.pos = vec(c*radius, s*radius) - vec(8, 8)
//            }
//        }
//        */
    }
}

