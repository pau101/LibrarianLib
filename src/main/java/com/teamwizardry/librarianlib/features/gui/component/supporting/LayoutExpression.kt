package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import no.birkett.kiwi.Expression
import no.birkett.kiwi.Strength
import no.birkett.kiwi.Symbol
import no.birkett.kiwi.Symbolics

sealed class ExpressionMetric
class XAxis private constructor(): ExpressionMetric()
class YAxis private constructor(): ExpressionMetric()
class Dimension private constructor(): ExpressionMetric()

abstract class LayoutExpression<T : ExpressionMetric> internal constructor(internal var involvedComponents: Set<GuiComponent>, dependsOn: List<LayoutExpression<*>>) {
    abstract val kiwiExpression: Expression
    abstract protected val _stringRepresentation: String

    private var stringRepresentationStorage: String? = null
    var stringRepresentation: String
        get() = stringRepresentationStorage ?: _stringRepresentation
        set(value) { stringRepresentationStorage = value}

    internal val dependants = mutableSetOf<() -> Unit>()
    init {
        dependsOn.forEach {
            it.dependants.add(this::change)
        }
    }

    fun change() {
        dependants.forEach { it() }
    }

    operator fun times(other: Number): LayoutExpression<T> = LayoutExpressionTimes(this, LayoutExpressionConstant(other))
    operator fun div(other: Number): LayoutExpression<T> = LayoutExpressionDiv(this, LayoutExpressionConstant(other))
    operator fun plus(other: Number): LayoutExpression<T> = LayoutExpressionPlus(this, LayoutExpressionConstant(other))
    operator fun minus(other: Number): LayoutExpression<T> = LayoutExpressionMinus(this, LayoutExpressionConstant(other))

    operator fun times(other: LayoutConstantCell): LayoutExpression<T> = LayoutExpressionTimes(this, LayoutExpressionMutableConstant(other))
    operator fun div(other: LayoutConstantCell): LayoutExpression<T> = LayoutExpressionDiv(this, LayoutExpressionMutableConstant(other))
    operator fun plus(other: LayoutConstantCell): LayoutExpression<T> = LayoutExpressionPlus(this, LayoutExpressionMutableConstant(other))
    operator fun minus(other: LayoutConstantCell): LayoutExpression<T> = LayoutExpressionMinus(this, LayoutExpressionMutableConstant(other))

    operator fun times(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionTimes(this, other)
    operator fun div(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionDiv(this, other)
    operator fun plus(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionPlus(this, other)
    operator fun minus(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionMinus(this, other)

    @JvmOverloads
    fun equalTo(other: LayoutExpression<T>, isActive: Boolean = true): LayoutConstraint {
        val root = getSolverRootComponent(other)
        val c = LayoutConstraintEqual(root, this, other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun gequalTo(other: LayoutExpression<T>, isActive: Boolean = true): LayoutConstraint {
        val root = getSolverRootComponent(other)
        val c = LayoutConstraintGreaterThanOrEqual(root, this, other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun lequalTo(other: LayoutExpression<T>, isActive: Boolean = true): LayoutConstraint {
        val root = getSolverRootComponent(other)
        val c = LayoutConstraintLessThanOrEqual(root, this, other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be equal to [other].
     */
    @JvmOverloads
    fun equalTo(other: Number, isActive: Boolean = true): LayoutConstraint {
        return equalTo(LayoutExpressionConstant(other), isActive)
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun gequalTo(other: Number, isActive: Boolean = true): LayoutConstraint {
        return gequalTo(LayoutExpressionConstant(other), isActive)
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun lequalTo(other: Number, isActive: Boolean = true): LayoutConstraint {
        return lequalTo(LayoutExpressionConstant(other), isActive)
    }

    /** Kotlin infix shortcut for [equalTo] */
    infix fun eq(other: LayoutExpression<T>) = equalTo(other)
    /** Kotlin infix shortcut for [lequalTo] */
    infix fun leq(other: LayoutExpression<T>) = lequalTo(other)
    /** Kotlin infix shortcut for [gequalTo] */
    infix fun geq(other: LayoutExpression<T>) = gequalTo(other)

    /** Kotlin infix shortcut for [equalTo] */
    infix fun eq(other: Number) = equalTo(other)
    /** Kotlin infix shortcut for [lequalTo] */
    infix fun leq(other: Number) = lequalTo(other)
    /** Kotlin infix shortcut for [gequalTo] */
    infix fun geq(other: Number) = gequalTo(other)

    private fun getSolverRootComponent(other: LayoutExpression<T>?): GuiComponent {

        val involved = if(other == null) this.involvedComponents else this.involvedComponents + other.involvedComponents
        if(involved.isEmpty()) throw IllegalArgumentException("Constraint uses no components")

        if(involved.any { it.layout.validSolvers.isEmpty() }) {
            throw IllegalArgumentException("Not all components are in valid solver contexts")
        }

        val candidates = involved.flatMap { it.layout.validSolvers }.toSet()

        val valid = candidates.toMutableSet()
        valid.removeIf { candidate -> involved.any { candidate !in it.layout.validSolvers } }

        if(valid.isEmpty()) {
            throw IllegalArgumentException("No common solvers")
        }

        return involved.find { it.layout.solver in valid } ?:
                involved.mapNotNull { it.layout.containingSolverComponent }.find { it.layout.solver in valid } ?:
                throw Exception("WTF")
    }

    override fun toString(): String {
        return stringRepresentation
    }
}

/**
 * Return an anchor multiplied by [other]
 */
operator fun <T: ExpressionMetric> Number.times(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionTimes(LayoutExpressionConstant(this), other)

/**
 * Return an anchor divided by [other]
 */
operator fun <T: ExpressionMetric> Number.div(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionDiv(LayoutExpressionConstant(this), other)

/**
 * Return an anchor increased by [other]
 */
operator fun <T: ExpressionMetric> Number.plus(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionPlus(LayoutExpressionConstant(this), other)

/**
 * Return an anchor reduced by [other]
 */
operator fun <T: ExpressionMetric> Number.minus(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionMinus(LayoutExpressionConstant(this), other)

class LayoutExpressionConstant<T: ExpressionMetric>(val const: Number) : LayoutExpression<T>(emptySet(), emptyList()) {
    override val kiwiExpression: Expression
        get() = Expression(const.toDouble())
    override val _stringRepresentation: String
        get() = "$const"
}

class LayoutExpressionMutableConstant<T: ExpressionMetric>(val const: LayoutConstantCell) : LayoutExpression<T>(emptySet(), emptyList()) {
    override val kiwiExpression: Expression
        get() = Expression(const.value.toDouble())
    override val _stringRepresentation: String
        get() = "{${const.value}}"

    init {
        const.onChange { this.change() }
    }
}

class LayoutExpressionPlus<T: ExpressionMetric>(val left: LayoutExpression<T>, val right: LayoutExpression<T>) : LayoutExpression<T>(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
    override val kiwiExpression: Expression
        get() = Symbolics.add(left.kiwiExpression, right.kiwiExpression)
    override val _stringRepresentation: String
        get() = "$left + $right"
}

class LayoutExpressionMinus<T: ExpressionMetric>(val left: LayoutExpression<T>, val right: LayoutExpression<T>) : LayoutExpression<T>(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
    override val kiwiExpression: Expression
        get() = Symbolics.subtract(left.kiwiExpression, right.kiwiExpression)
    override val _stringRepresentation: String
        get() = "$left - $right"
}

class LayoutExpressionTimes<T: ExpressionMetric>(val left: LayoutExpression<T>, val right: LayoutExpression<T>) : LayoutExpression<T>(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
    init {
        if(left.involvedComponents.isNotEmpty() && right.involvedComponents.isNotEmpty()) {
            throw IllegalArgumentException("Cannot multiply variables by other variables: `$left` * `$right`")
        }
    }
    override val kiwiExpression: Expression
        get() = Symbolics.multiply(left.kiwiExpression, right.kiwiExpression)
    override val _stringRepresentation: String
        get() = "$leftStr * $rightStr"

    private val leftStr: String
        get() = if(left is LayoutExpressionPlus || left is LayoutExpressionMinus) "($left)" else "$left"
    private val rightStr: String
        get() = if(right is LayoutExpressionPlus || right is LayoutExpressionMinus) "($right)" else "$right"
}

class LayoutExpressionDiv<T: ExpressionMetric>(val left: LayoutExpression<T>, val right: LayoutExpression<T>) : LayoutExpression<T>(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
    init {
        if(right.involvedComponents.isNotEmpty()) {
            throw IllegalArgumentException("Cannot divide by variables: `$left` / `$right`")
        }
    }
    override val kiwiExpression: Expression
        get() = Symbolics.multiply(left.kiwiExpression, right.kiwiExpression)
    override val _stringRepresentation: String
        get() = "$leftStr / $rightStr"

    private val leftStr: String
        get() = if(left is LayoutExpressionPlus || left is LayoutExpressionMinus) "($left)" else "$left"
    private val rightStr: String
        get() = if(right is LayoutExpressionPlus || right is LayoutExpressionMinus) "($right)" else "$right"
}

class LayoutExpressionNegate<T: ExpressionMetric>(val expr: LayoutExpression<T>) : LayoutExpression<T>(expr.involvedComponents, listOf(expr)) {
    override val kiwiExpression: Expression
        get() = Symbolics.negate(expr.kiwiExpression)
    override val _stringRepresentation: String
        get() = "-$exprStr"

    private val exprStr: String
        get() = if(expr is LayoutExpressionPlus || expr is LayoutExpressionMinus) "($expr)" else "$expr"
}
