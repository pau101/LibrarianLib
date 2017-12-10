package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import no.birkett.kiwi.*

class Anchor(internal val component: GuiComponent, val name: String, internal var variable: Variable = Variable(0.0)) : LayoutExpression(Expression(Term(variable)), setOf(component), "", false) {
    override var stringRepresentation: String
        get() = component.relationships.guiPath() + "@" + System.identityHashCode(component).toString(16) +  "#" + name
        set(value) {}
}