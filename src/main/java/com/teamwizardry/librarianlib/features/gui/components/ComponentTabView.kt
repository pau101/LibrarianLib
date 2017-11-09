package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent

class ComponentTabView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    private val tabs = mutableMapOf<String, GuiComponent>()
    private val contents = mutableMapOf<String, GuiComponent>()

    private val tabStack = ComponentStackView(true, 0, 0, width, 0)
    private val contentsContainer = ComponentVoid(0, 0, width, height)

    init {
        add(tabStack, contentsContainer)

        tabStack.layout.left.equalTo(this.layout.left)
        tabStack.layout.right.equalTo(this.layout.right)
        tabStack.layout.top.equalTo(this.layout.top)

        contentsContainer.layout.left.equalTo(this.layout.left)
        contentsContainer.layout.right.equalTo(this.layout.right)
        contentsContainer.layout.bottom.equalTo(this.layout.bottom)
        contentsContainer.layout.top.equalTo(tabStack.layout.bottom)
    }

    fun add(key: String, tab: GuiComponent, content: GuiComponent) {
        tabs[key] = tab
        contents[key] = content

        content.isVisible = false

        contentsContainer.add(content)
        tabStack.add(tab)
    }
}