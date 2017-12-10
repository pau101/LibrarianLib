package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import no.birkett.kiwi.*

class LayoutConstraint(val kiwiConstraint: Constraint, val solver: Solver, strength: Double, val stringRepresentation: String) {
    /**
     * Whether this constraint should have an effect
     */
    var isActive: Boolean = false
        set(value) {
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
    var strength = strength
        set(value) {
            if(value != field) {
                remove()
                kiwiConstraint.setStrength(strength)
                add()
            }
        }

    private fun add() {
        try {
            solver.addConstraint(kiwiConstraint)
        } catch (e: UnsatisfiableConstraintException) {
            LibrarianLog.error("Unsatisfiable constraint: `${this.stringRepresentation}` strength: ${Strength.name(strength)}")
            LibrarianLog.errorStackTrace(e)
        }
    }

    private fun remove() {
        try {
            solver.removeConstraint(kiwiConstraint)
        } catch (e: UnknownConstraintException) {
            // NOOP
        }
    }
}