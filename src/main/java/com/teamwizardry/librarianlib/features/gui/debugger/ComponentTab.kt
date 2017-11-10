package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentSpriteCapped
import com.teamwizardry.librarianlib.features.gui.components.ComponentSpriteTiled
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import no.birkett.kiwi.Strength

class ComponentTab(val tabString: String, posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    private val background = ComponentSprite(TAB_BACKGROUND, 0, 0)
    private val text = ComponentText(0, 0, ComponentText.TextAlignH.CENTER)

    init {
        this.layout.width.strength = Strength.STRONG
        this.layout.height.strength = Strength.STRONG
        text.text(tabString)
        text.sizeToText()

        add(background, text)

        background.layout.top.equalTo(this.layout.top)
        background.layout.bottom.equalTo(this.layout.bottom)
        background.layout.left.equalTo(this.layout.left)
        background.layout.right.equalTo(this.layout.right)

        text.layout.left.equalTo(this.layout.left)
        text.layout.right.equalTo(this.layout.right)
        text.layout.centerY.equalTo(this.layout.centerY)

        background.name = this.name
        text.name = this.name
    }

    companion object {
        val TAB_BACKGROUND = DebuggerConst.TEXTURE.getSprite("tab_background", 16, 13)
    }
}