package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentStackView
import org.fusesource.jansi.Ansi
import java.awt.Color

/**
 * TODO: Document file ComponentDebugPanel
 *
 * Created by TheCodeWarrior
 */
class ComponentDebugPanel : GuiComponent(0, 0, 100, 100) {
    val rect = ComponentRect(0, 0, 1, 1)
    val stack = ComponentStackView(true,  10, 10, 100, 30)
    init {
        add(rect)
        rect.layout.top.equalTo(this.layout.top)
        rect.layout.bottom.equalTo(this.layout.bottom)

        rect.layout.left.equalTo(this.layout.left)
        rect.layout.right.equalTo(this.layout.right)

        stack.layout.top.equalTo(this.layout.top, 15.0)
        stack.layout.left.equalTo(this.layout.left, 15.0)
        stack.layout.right.equalTo(this.layout.right, -15.0)
        add(stack)

        val names = arrayOf(
                "Foo",
                "Bar",
                "Baz",
                "And some more stuff to go in here"
        )

        names.forEach { name ->
            val r = ComponentTab(name, 0, 0, 10, 13)
            r.name = "tab"
            stack.add(r)
        }
    }

    @Hook("tab")
    fun fooClicked(e: GuiComponentEvents.MouseClickEvent) {
        print(e.component.pos.x)
    }
}
