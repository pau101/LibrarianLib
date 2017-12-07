package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import no.birkett.kiwi.Expression
import no.birkett.kiwi.Solver
import no.birkett.kiwi.Strength
import no.birkett.kiwi.Symbolics

open class LayoutExpression internal constructor(internal val kiwiExpression: Expression, internal val involvedComponents: Set<GuiComponent>,
                                                 open internal val stringRepresentation: String, internal val isAddition: Boolean) {
    /**
     * Return an anchor multiplied by [other]
     */
    operator fun times(other: Number): LayoutExpression {
        return LayoutExpression(Symbolics.multiply(kiwiExpression, other.toDouble()), involvedComponents,
                (if(this.isAddition) "(${this.stringRepresentation})" else this.stringRepresentation) + " * $other",
                false)
    }

    /**
     * Return an anchor divided by [other]
     */
    operator fun div(other: Number): LayoutExpression {
        return LayoutExpression(Symbolics.divide(kiwiExpression, other.toDouble()), involvedComponents,
                (if(this.isAddition) "(${this.stringRepresentation})" else this.stringRepresentation) + " / $other",
                false)
    }

    /**
     * Return an anchor increased by [other]
     */
    operator fun plus(other: Number): LayoutExpression {
        return LayoutExpression(Symbolics.add(kiwiExpression, other.toDouble()), involvedComponents,
                "${this.stringRepresentation} + $other",
                true)
    }

    /**
     * Return an anchor reduced by [other]
     */
    operator fun minus(other: Number): LayoutExpression {
        return LayoutExpression(Symbolics.subtract(kiwiExpression, other.toDouble()), involvedComponents,
                "${this.stringRepresentation} - $other",
                true)
    }

    /**
     * Return an anchor multiplied by [other]
     */
    operator fun times(other: LayoutExpression): Nothing = throw UnsupportedOperationException("Constraints do not support multiplying variables by variables")

    /**
     * Return an anchor divided by [other]
     */
    operator fun div(other: LayoutExpression): Nothing = throw UnsupportedOperationException("Constraints do not support dividing by variables")

    /**
     * Return an anchor increased by [other]
     */
    operator fun plus(other: LayoutExpression): LayoutExpression {
        return LayoutExpression(Symbolics.add(kiwiExpression, other.kiwiExpression), involvedComponents + other.involvedComponents,
                "${this.stringRepresentation} + ${other.stringRepresentation}",
                true)
    }

    /**
     * Return an anchor reduced by [other]
     */
    operator fun minus(other: LayoutExpression): LayoutExpression {
        return LayoutExpression(Symbolics.subtract(kiwiExpression, other.kiwiExpression), involvedComponents + other.involvedComponents,
                "${this.stringRepresentation} - ${other.stringRepresentation}",
                true)
    }

    /**
     * Set this anchor to be equal to [other].
     */
    @JvmOverloads
    fun equalTo(other: LayoutExpression, isActive: Boolean = true): LayoutConstraint {
        val c = LayoutConstraint(Symbolics.equals(this.kiwiExpression, other.kiwiExpression), getSolver(other), Strength.REQUIRED,
                this.stringRepresentation + " == " + other.stringRepresentation)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun gequalTo(other: LayoutExpression, isActive: Boolean = true): LayoutConstraint {
        val c = LayoutConstraint(Symbolics.greaterThanOrEqualTo(this.kiwiExpression, other.kiwiExpression), getSolver(other), Strength.REQUIRED,
                this.stringRepresentation + " >= " + other.stringRepresentation)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun lequalTo(other: LayoutExpression, isActive: Boolean = true): LayoutConstraint {
        val c = LayoutConstraint(Symbolics.lessThanOrEqualTo(this.kiwiExpression, other.kiwiExpression), getSolver(other), Strength.REQUIRED,
                this.stringRepresentation + " <= " + other.stringRepresentation)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be equal to [other].
     */
    @JvmOverloads
    fun equalTo(other: Number, isActive: Boolean = true): LayoutConstraint {
        val c = LayoutConstraint(Symbolics.equals(this.kiwiExpression, other.toDouble()), getSolver(null), Strength.REQUIRED,
                this.stringRepresentation + " == " + other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun gequalTo(other: Number, isActive: Boolean = true): LayoutConstraint {
        val c = LayoutConstraint(Symbolics.greaterThanOrEqualTo(this.kiwiExpression, other.toDouble()), getSolver(null), Strength.REQUIRED,
                this.stringRepresentation + " >= " + other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun lequalTo(other: Number, isActive: Boolean = true): LayoutConstraint {
        val c = LayoutConstraint(Symbolics.lessThanOrEqualTo(this.kiwiExpression, other.toDouble()), getSolver(null), Strength.REQUIRED,
                this.stringRepresentation + " <= " + other)
        c.isActive = isActive
        return c
    }

    /** Kotlin infix shortcut for [equalTo] */
    infix fun eq(other: LayoutExpression) = equalTo(other)
    /** Kotlin infix shortcut for [lequalTo] */
    infix fun leq(other: LayoutExpression) = lequalTo(other)
    /** Kotlin infix shortcut for [gequalTo] */
    infix fun geq(other: LayoutExpression) = gequalTo(other)

    /** Kotlin infix shortcut for [equalTo] */
    infix fun eq(other: Number) = equalTo(other)
    /** Kotlin infix shortcut for [lequalTo] */
    infix fun leq(other: Number) = lequalTo(other)
    /** Kotlin infix shortcut for [gequalTo] */
    infix fun geq(other: Number) = gequalTo(other)

    private fun getSolver(other: LayoutExpression?): Solver {
        var solver: Solver? = null
        val involved = if(other == null) this.involvedComponents else this.involvedComponents + other.involvedComponents
        if(involved.any { it.layout.rootSolver == null }) {
            throw IllegalArgumentException("Not all components are in valid solver contexts!")
        }
        involved.forEach {
            if(solver == null) {
                solver = it.layout.rootSolver
            } else {
                if(it.layout.rootSolver != solver) {
                    throw IllegalArgumentException("Not all components are in the same solver context! Are any of them in baked or isolated contexts?")
                }
            }
        }

        return solver!!
    }

    override fun toString(): String {
        return stringRepresentation
    }
}

/**
 * Return an anchor multiplied by [other]
 */
operator fun Number.times(other: LayoutExpression): LayoutExpression {
    return LayoutExpression(Symbolics.multiply(this.toDouble(), other.kiwiExpression), other.involvedComponents,
            "$this * " + (if(other.isAddition) "(${other.stringRepresentation})" else other.stringRepresentation),
            false)
}

/**
 * Return an anchor divided by [other]
 */
operator fun Number.div(other: LayoutExpression): Nothing = throw UnsupportedOperationException("Constraints do not support dividing by variables")

/**
 * Return an anchor increased by [other]
 */
operator fun Number.plus(other: LayoutExpression): LayoutExpression {
    return LayoutExpression(Symbolics.add(this.toDouble(), other.kiwiExpression), other.involvedComponents,
            "$this + ${other.stringRepresentation}",
            true)
}

/**
 * Return an anchor reduced by [other]
 */
operator fun Number.minus(other: LayoutExpression): LayoutExpression {
    return LayoutExpression(Symbolics.subtract(this.toDouble(), other.kiwiExpression), other.involvedComponents,
            "$this - ${other.stringRepresentation}",
            true)
}
