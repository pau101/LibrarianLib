package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
class Term @JvmOverloads constructor(var variable: Variable, var coefficient: Double = 1.0) {

    val value: Double
        get() = coefficient * variable.value

    override fun toString(): String {
        return "variable: ($variable) coefficient: $coefficient"
    }
}
