package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentSpriteCapped
import com.teamwizardry.librarianlib.features.gui.components.ComponentSpriteTiled
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import no.birkett.kiwi.Strength

class ComponentTab(val tabString: String, posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    private val background = ComponentSprite(TAB_BACKGROUND, 0, 0)
    private val text = ComponentText(0, 0).centered()

    init {
        this.layout.widthStay = Strength.STRONG
        this.layout.heightStay = Strength.STRONG
        text.text = tabString

        add(background, text)

        layout {
            background.layout.top eq this.layout.top
            background.layout.bottom eq this.layout.bottom
            background.layout.left eq this.layout.left
            background.layout.right eq this.layout.right

            text.layout.left eq this.layout.left
            text.layout.right eq this.layout.right
            text.layout.centerY eq this.layout.centerY
        }

        background.name = this.name
        text.name = this.name
    }

    companion object {
        val TAB_BACKGROUND = DebuggerConst.TEXTURE.getSprite("tab_background", 16, 13)
    }
}