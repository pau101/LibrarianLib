package no.birkett.kiwi

import java.util.ArrayList

/**
 * Created by alex on 30/01/15.
 */
class Expression {

    var terms: MutableList<Term>

    var constant: Double = 0.0

    @JvmOverloads constructor(constant: Double = 0.0) {
        this.constant = constant
        this.terms = mutableListOf()
    }

    @JvmOverloads constructor(term: Term, constant: Double = 0.0) {
        val _terms = mutableListOf<Term>()
        _terms.add(term)
        terms = _terms
        this.constant = constant
    }

    @JvmOverloads constructor(terms: List<Term>, constant: Double = 0.0) {
        this.terms = terms.toMutableList()
        this.constant = constant
    }

    val value: Double
        get() {
            var result = this.constant

            for (term in terms) {
                result += term.value
            }
            return result
        }

    val isConstant: Boolean
        get() = terms.size == 0

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("isConstant: $isConstant constant: $constant")
        if (!isConstant) {
            sb.append(" terms: [")
            for (term in terms) {
                sb.append("(")
                sb.append(term)
                sb.append(")")
            }
            sb.append("] ")
        }
        return sb.toString()
    }

}

