package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import no.birkett.kiwi.*

abstract class LayoutConstraint(internal val solverComponent: GuiComponent) {
    abstract protected val _kiwiConstraint: Constraint
    abstract val stringRepresentation: String
    abstract internal val involvedComponents: Set<GuiComponent>

    internal val solver = solverComponent.layout.solver!!
    lateinit internal var kiwiConstraint: Constraint

    /**
     * Whether this constraint should have an effect
     */
    var isActive: Boolean = false
        set(value) {
            if(!valid) return
            if(value != field) {
                if(value)
                    add()
                else
                    remove()
                field = value
            }
        }

    /**
     * Used to determine which constraints can be broken in order to maintain others
     *
     * @see no.birkett.kiwi.Strength
     */
    var strength = Strength.REQUIRED
        set(value) {
            if(!valid) return
            if(value != field) {
                remove(false)
                kiwiConstraint.setStrength(strength)
                add()
            }
        }

    /**
     * Set to false when one of the involved components is removed from the component hierarchy. At that point the
     * constraint will no longer function.
     */
    var valid = true
        internal set

    internal fun change() {
        if(isActive) {
            try {
                remove(false)
                add()
            } catch (e: UnsatisfiableConstraintException) {
                LibrarianLog.error("Unsatisfiable constraint: `${this.stringRepresentation}` strength: ${Strength.name(strength)}")
                LibrarianLog.errorStackTrace(e)
            }
        }
    }

    private fun add() {
        kiwiConstraint = _kiwiConstraint
        try {
            solver.addConstraint(kiwiConstraint)
            solverComponent.layout.constraints.add(this)
        } catch (e: UnsatisfiableConstraintException) {
            LibrarianLog.error("Unsatisfiable constraint: `${this.stringRepresentation}` strength: ${Strength.name(strength)}")
            LibrarianLog.errorStackTrace(e)
        }
    }

    private fun remove(solve: Boolean = true) {
        if(!solver.hasConstraint(kiwiConstraint)) return
        try {
            solver.removeConstraint(kiwiConstraint, solve)
            solverComponent.layout.constraints.remove(this)
        } catch (e: UnknownConstraintException) {
            // NOOP
        }
    }
}

class LayoutConstraintEqual(val root: GuiComponent, val left: LayoutExpression<*>, val right: LayoutExpression<*>) : LayoutConstraint(root) {
    override val _kiwiConstraint: Constraint
        get() = Symbolics.equals(left.kiwiExpression, right.kiwiExpression)
    override val stringRepresentation: String
        get() = "$left == $right"

    override val involvedComponents = left.involvedComponents + right.involvedComponents

    init {
        left.dependants.add(this::change)
        right.dependants.add(this::change)
        kiwiConstraint = _kiwiConstraint
    }
}

class LayoutConstraintLessThanOrEqual(val root: GuiComponent, val left: LayoutExpression<*>, val right: LayoutExpression<*>) : LayoutConstraint(root) {
    override val _kiwiConstraint: Constraint
        get() = Symbolics.lessThanOrEqualTo(left.kiwiExpression, right.kiwiExpression)
    override val stringRepresentation: String
        get() = "$left <= $right"

    override val involvedComponents = left.involvedComponents + right.involvedComponents

    init {
        left.dependants.add(this::change)
        right.dependants.add(this::change)
        kiwiConstraint = _kiwiConstraint
    }
}

class LayoutConstraintGreaterThanOrEqual(val root: GuiComponent, val left: LayoutExpression<*>, val right: LayoutExpression<*>) : LayoutConstraint(root) {
    override val _kiwiConstraint: Constraint
        get() = Symbolics.greaterThanOrEqualTo(left.kiwiExpression, right.kiwiExpression)
    override val stringRepresentation: String
        get() = "$left >= $right"

    override val involvedComponents = left.involvedComponents + right.involvedComponents

    init {
        left.dependants.add(this::change)
        right.dependants.add(this::change)
        kiwiConstraint = _kiwiConstraint
    }
}
