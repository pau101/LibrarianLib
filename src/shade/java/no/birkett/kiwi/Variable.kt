package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
class Variable {

    var name: String? = null

    var value: Double = 0.0

    constructor(name: String) {
        this.name = name
    }

    constructor(value: Double) {
        this.value = value
    }

    override fun toString(): String {
        return "name: $name value: $value"
    }

}
