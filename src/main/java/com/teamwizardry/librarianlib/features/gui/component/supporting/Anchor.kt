package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import no.birkett.kiwi.*

class Anchor(internal val component: GuiComponent, val axis: Vec2d.Axis, val multiplier: Double = 1.0, val constant: Double = 0.0,
             internal var variable: Variable = Variable(0.0), internal var relativeVariable: Variable = Variable(0.0)) {

    internal fun setName(name: String) {
        variable.name = name
        if(relativeVariable != variable)
            relativeVariable.name = name + ".relative"
    }

    fun makeExpression(): Expression {
        val parentScaleFactor = component.parent?.geometry?.getScaleFactor()?.getAxis(axis) ?: 1.0

        if(relativeVariable !== variable && multiplier != 1.0) {
            return Expression(
                    listOf(Term(variable), Term(relativeVariable, multiplier - 1)),
                    constant * parentScaleFactor
            )
        }
        return Expression(
                Term(variable, multiplier),
                constant * parentScaleFactor
        )
    }

    /**
     * Return an anchor multiplied by [other]
     *
     * (value is in the parent's coordinate space)
     */
    operator fun times(other: Number): Anchor {
        return Anchor(component, axis, multiplier * other.toDouble(), constant * other.toDouble(), variable, relativeVariable)
    }

    /**
     * Return an anchor divided by [other]
     *
     * (value is in the parent's coordinate space)
     */
    operator fun div(other: Number): Anchor {
        return Anchor(component, axis, multiplier / other.toDouble(), constant / other.toDouble(), variable, relativeVariable)
    }

    /**
     * Return an anchor increased by [other]
     *
     * (value is in the parent's coordinate space)
     */
    operator fun plus(other: Number): Anchor {
        return Anchor(component, axis, multiplier, constant + other.toDouble(), variable, relativeVariable)
    }

    /**
     * Return an anchor reduced by [other]
     *
     * (value is in the parent's coordinate space)
     */
    operator fun minus(other: Number): Anchor {
        return Anchor(component, axis, multiplier, constant - other.toDouble(), variable, relativeVariable)
    }

    /**
     * Set this anchor to be equal to [other]
     */
    fun equalTo(other: Anchor): LayoutConstraint {
        val c = LayoutConstraint(this, other, LayoutConstraint.LayoutOperator.EQUAL, Strength.REQUIRED)
        component.layout.add(c)
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    fun gequalTo(other: Anchor): LayoutConstraint {
        val c = LayoutConstraint(this, other, LayoutConstraint.LayoutOperator.EQUAL, Strength.REQUIRED)
        component.layout.add(c)
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    fun lequalTo(other: Anchor): LayoutConstraint {
        val c = LayoutConstraint(this, other, LayoutConstraint.LayoutOperator.EQUAL, Strength.REQUIRED)
        component.layout.add(c)
        return c
    }

    /**
     * How strongly this constraint's existing value should be held. Default: [Strength.WEAK]
     */
    var strength = Strength.WEAK

    /** Kotlin infix shortcut for [equalTo] */
    infix fun eq(other: Anchor) = equalTo(other)
    /** Kotlin infix shortcut for [lequalTo] */
    infix fun leq(other: Anchor) = lequalTo(other)
    /** Kotlin infix shortcut for [gequalTo] */
    infix fun geq(other: Anchor) = gequalTo(other)
}