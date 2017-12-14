package no.birkett.kiwi

import java.util.*

/**
 * Created by alex on 30/01/15.
 */
class Constraint {

    var expression: Expression
    var strength: Strength = Strength.NONE
        private set
    var op: RelationalOperator

    @JvmOverloads constructor(expr: Expression, op: RelationalOperator, strength: Strength = Strength.REQUIRED) {
        this.expression = reduce(expr)
        this.op = op
        this.strength = strength
    }

    constructor(other: Constraint, strength: Strength) : this(other.expression, other.op, strength) {}

    private fun reduce(expr: Expression): Expression {

        val vars = LinkedHashMap<Variable, Double>()
        for (term in expr.terms) {
            var value: Double? = vars[term.variable]
            if (value == null) {
                value = 0.0
            }
            value += term.coefficient
            vars.put(term.variable, value)
        }

        val reducedTerms = ArrayList<Term>()
        for (variable in vars.keys) {
            reducedTerms.add(Term(variable, vars[variable]!!))
        }

        return Expression(reducedTerms, expr.constant)
    }

    fun setStrength(strength: Strength): Constraint {
        this.strength = strength
        return this
    }

    override fun toString(): String {
        return "expression: ($expression) strength: $strength operator: $op"
    }

}
