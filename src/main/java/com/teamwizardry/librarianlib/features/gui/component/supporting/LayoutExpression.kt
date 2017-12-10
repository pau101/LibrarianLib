package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import no.birkett.kiwi.Expression
import no.birkett.kiwi.Strength
import no.birkett.kiwi.Symbol
import no.birkett.kiwi.Symbolics

abstract class LayoutExpression internal constructor(internal var involvedComponents: Set<GuiComponent>, dependsOn: List<LayoutExpression>) {
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

    /**
     * Return an anchor multiplied by [other]
     */
    operator fun times(other: Number): LayoutExpression = LayoutExpressionTimes(this, LayoutExpressionConstant(other))

    /**
     * Return an anchor divided by [other]
     */
    operator fun div(other: Number): LayoutExpression = LayoutExpressionDiv(this, LayoutExpressionConstant(other))

    /**
     * Return an anchor increased by [other]
     */
    operator fun plus(other: Number): LayoutExpression = LayoutExpressionPlus(this, LayoutExpressionConstant(other))

    /**
     * Return an anchor reduced by [other]
     */
    operator fun minus(other: Number): LayoutExpression = LayoutExpressionMinus(this, LayoutExpressionConstant(other))

    /**
     * Return an anchor multiplied by [other]
     */
    operator fun times(other: LayoutConstantCell): LayoutExpression = LayoutExpressionTimes(this, LayoutExpressionMutableConstant(other))

    /**
     * Return an anchor divided by [other]
     */
    operator fun div(other: LayoutConstantCell): LayoutExpression = LayoutExpressionDiv(this, LayoutExpressionMutableConstant(other))

    /**
     * Return an anchor increased by [other]
     */
    operator fun plus(other: LayoutConstantCell): LayoutExpression = LayoutExpressionPlus(this, LayoutExpressionMutableConstant(other))

    /**
     * Return an anchor reduced by [other]
     */
    operator fun minus(other: LayoutConstantCell): LayoutExpression = LayoutExpressionMinus(this, LayoutExpressionMutableConstant(other))

    /**
     * Return an anchor multiplied by [other]
     */
    operator fun times(other: LayoutExpression): LayoutExpression = LayoutExpressionTimes(this, other)

    /**
     * Return an anchor divided by [other]
     */
    operator fun div(other: LayoutExpression): LayoutExpression = LayoutExpressionDiv(this, other)

    /**
     * Return an anchor increased by [other]
     */
    operator fun plus(other: LayoutExpression): LayoutExpression = LayoutExpressionPlus(this, other)

    /**
     * Return an anchor reduced by [other]
     */
    operator fun minus(other: LayoutExpression): LayoutExpression = LayoutExpressionMinus(this, other)

    /**
     * Set this anchor to be equal to [other].
     */
    @JvmOverloads
    fun equalTo(other: LayoutExpression, isActive: Boolean = true): LayoutConstraint {
        val root = getSolverRootComponent(other)
        val c = LayoutConstraintEqual(root, this, other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun gequalTo(other: LayoutExpression, isActive: Boolean = true): LayoutConstraint {
        val root = getSolverRootComponent(other)
        val c = LayoutConstraintGreaterThanOrEqual(root, this, other)
        c.isActive = isActive
        return c
    }

    /**
     * Set this anchor to be greater than or equal to [other]
     */
    @JvmOverloads
    fun lequalTo(other: LayoutExpression, isActive: Boolean = true): LayoutConstraint {
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

    private fun getSolverRootComponent(other: LayoutExpression?): GuiComponent {

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
operator fun Number.times(other: LayoutExpression): LayoutExpression = LayoutExpressionTimes(LayoutExpressionConstant(this), other)

/**
 * Return an anchor divided by [other]
 */
operator fun Number.div(other: LayoutExpression): LayoutExpression = LayoutExpressionDiv(LayoutExpressionConstant(this), other)

/**
 * Return an anchor increased by [other]
 */
operator fun Number.plus(other: LayoutExpression): LayoutExpression  = LayoutExpressionPlus(LayoutExpressionConstant(this), other)

/**
 * Return an anchor reduced by [other]
 */
operator fun Number.minus(other: LayoutExpression): LayoutExpression  = LayoutExpressionMinus(LayoutExpressionConstant(this), other)

class LayoutExpressionConstant(val const: Number) : LayoutExpression(emptySet(), emptyList()) {
    override val kiwiExpression: Expression
        get() = Expression(const.toDouble())
    override val _stringRepresentation: String
        get() = "$const"
}

class LayoutExpressionMutableConstant(val const: LayoutConstantCell) : LayoutExpression(emptySet(), emptyList()) {
    override val kiwiExpression: Expression
        get() = Expression(const.value.toDouble())
    override val _stringRepresentation: String
        get() = "{${const.value}}"

    init {
        const.onChange { this.change() }
    }
}

class LayoutExpressionPlus(val left: LayoutExpression, val right: LayoutExpression) : LayoutExpression(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
    override val kiwiExpression: Expression
        get() = Symbolics.add(left.kiwiExpression, right.kiwiExpression)
    override val _stringRepresentation: String
        get() = "$left + $right"
}

class LayoutExpressionMinus(val left: LayoutExpression, val right: LayoutExpression) : LayoutExpression(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
    override val kiwiExpression: Expression
        get() = Symbolics.subtract(left.kiwiExpression, right.kiwiExpression)
    override val _stringRepresentation: String
        get() = "$left - $right"
}

class LayoutExpressionTimes(val left: LayoutExpression, val right: LayoutExpression) : LayoutExpression(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
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

class LayoutExpressionDiv(val left: LayoutExpression, val right: LayoutExpression) : LayoutExpression(left.involvedComponents + right.involvedComponents, listOf(left, right)) {
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

class LayoutExpressionNegate(val expr: LayoutExpression) : LayoutExpression(expr.involvedComponents, listOf(expr)) {
    override val kiwiExpression: Expression
        get() = Symbolics.negate(expr.kiwiExpression)
    override val _stringRepresentation: String
        get() = "-$exprStr"

    private val exprStr: String
        get() = if(expr is LayoutExpressionPlus || expr is LayoutExpressionMinus) "($expr)" else "$expr"
}
