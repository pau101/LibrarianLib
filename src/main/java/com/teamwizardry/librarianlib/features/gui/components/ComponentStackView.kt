package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.withX
import com.teamwizardry.librarianlib.features.kotlin.withY
import no.birkett.kiwi.Constraint
import no.birkett.kiwi.Strength
import no.birkett.kiwi.Symbolics

class ComponentStackView(val horizontal: Boolean, posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    init {
        this.layout.height.strength = Strength.STRONG
    }
    private fun layout() {
        if(horizontal) {
            val width = (this.size.x / this.children.size).toInt().toDouble()
            val height = this.children.map { it.size.y }.max() ?: 0.0
            this.size = this.size.withY(height)
            var x = 0.0
            this.children.forEach {
                it.pos = vec(x, 0)
                val w = if(it == this.children.last()) this.size.x - x else width
                it.size = vec(w, height)
                x += width
            }
        } else {
            val height = (this.size.y / this.children.size).toInt().toDouble()
            val width = this.children.map { it.size.x }.max() ?: 0.0
            this.size = this.size.withX(width)
            var y = 0.0
            this.children.forEach {
                it.pos = vec(0, y)
                val h = if(it == this.children.last()) this.size.y - y else height
                it.size = vec(width, h)
                y += height
            }
        }
    }

    @Hook
    private fun preLayout(e: GuiComponentEvents.PreLayoutEvent) {
        layout()
    }

    @Hook
    private fun preDraw(e: GuiComponentEvents.PreDrawEvent) {
        layout()
    }

    private fun constraints(e: GuiComponentEvents.AddConstraintsEvent) {
        if(horizontal) {
            val last = this.children.fold(this.layout.left) { last, child ->
                e.solver.addConstraint(Symbolics.equals(child.layout.left, last))
                return@fold child.layout.right
            }
            e.solver.addConstraint(Symbolics.equals(last, this.layout.right))

            this.children.forEach { child ->
                e.solver.addConstraint(Symbolics.equals(child.layout.top, this.layout.top))
                e.solver.addConstraint(Symbolics.equals(child.layout.bottom, this.layout.bottom))
            }
        } else {
            val last = this.children.fold(this.layout.top) { last, child ->
                e.solver.addConstraint(Symbolics.equals(child.layout.top, last))
                return@fold child.layout.bottom
            }
            e.solver.addConstraint(Symbolics.equals(last, this.layout.bottom))

            this.children.forEach { child ->
                e.solver.addConstraint(Symbolics.equals(child.layout.left, this.layout.left))
                e.solver.addConstraint(Symbolics.equals(child.layout.right, this.layout.right))
            }
        }
    }

}