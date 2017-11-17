package com.teamwizardry.librarianlib.features.gui.component.supporting

import no.birkett.kiwi.Constraint
import no.birkett.kiwi.Symbolics

class LayoutConstraint(private val left: Anchor, private val right: Anchor, private val op: LayoutOperator, strength: Double) {
    /**
     * Whether this constraint should have an effect
     */
    var isActive = true

    /**
     * Used to determine which constraints can be broken in order to maintain others
     *
     * @see no.birkett.kiwi.Strength
     */
    var strength = strength

    fun makeConstraint(): Constraint {
        return when(op) {
            LayoutOperator.EQUAL ->
                Symbolics.equals(left.makeExpression(), right.makeExpression())
            LayoutOperator.LEQUAL ->
                Symbolics.lessThanOrEqualTo(left.makeExpression(), right.makeExpression())
            LayoutOperator.GEQUAL ->
                Symbolics.greaterThanOrEqualTo(left.makeExpression(), right.makeExpression())
        }
    }


    enum class LayoutOperator { EQUAL, LEQUAL, GEQUAL}
}