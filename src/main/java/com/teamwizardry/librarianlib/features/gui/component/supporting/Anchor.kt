package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import no.birkett.kiwi.*

class Anchor(internal val component: GuiComponent, val name: String, internal var variable: Variable = Variable(0.0)) : LayoutExpression(setOf(component), emptyList()) {
    override val kiwiExpression: Expression
        get() = Expression(Term(variable))
    override val _stringRepresentation: String
        get() = "$component#$name"
}