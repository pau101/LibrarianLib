package com.teamwizardry.librarianlib.features.gui.component.supporting

import no.birkett.kiwi.Constraint
import no.birkett.kiwi.Strength
import no.birkett.kiwi.Symbolics
import no.birkett.kiwi.Variable

class Anchor(private val layout: ComponentLayoutHandler): Variable(0.0) {
    /**
     * How strongly this constraint should be held. Default: [Strength.WEAK]
     */
    var strength = Strength.WEAK

    /**
     * Set this anchor to be equal to [other] + [constant] with [strength]
     *
     * [constant] is 0 by default
     * [strength] is [Strength.REQUIRED] by default
     */
    @JvmOverloads
    fun equalTo(other: Anchor, constant: Double = 0.0, strength: Double = Strength.REQUIRED): Constraint {
        layout.enabled = true
        other.layout.enabled = true
        val c: Constraint = if(constant == 0.0) {
            Symbolics.equals(this, other)
        } else {
            Symbolics.equals(this, Symbolics.add(other, constant))
        }
        layout.add(c)
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other] + [constant] with [strength]
     *
     * [constant] is 0 by default
     * [strength] is [Strength.REQUIRED] by default
     */
    @JvmOverloads
    fun greaterThanOrEqualTo(other: Anchor, constant: Double = 0.0, strength: Double = Strength.REQUIRED): Constraint {
        layout.enabled = true
        other.layout.enabled = true
        val c: Constraint = if(constant == 0.0) {
            Symbolics.greaterThanOrEqualTo(this, other)
        } else {
            Symbolics.greaterThanOrEqualTo(this, Symbolics.add(other, constant))
        }
        layout.add(c)
        return c
    }

    /**
     * Set this anchor to be less than or equal to [other] + [constant] with [strength]
     *
     * [constant] is 0 by default
     * [strength] is [Strength.REQUIRED] by default
     */
    @JvmOverloads
    fun lessThanOrEqualTo(other: Anchor, constant: Double = 0.0, strength: Double = Strength.REQUIRED): Constraint {
        layout.enabled = true
        other.layout.enabled = true
        val c: Constraint = if(constant == 0.0) {
            Symbolics.lessThanOrEqualTo(this, other)
        } else {
            Symbolics.lessThanOrEqualTo(this, Symbolics.add(other, constant))
        }
        layout.add(c)
        return c
    }
}