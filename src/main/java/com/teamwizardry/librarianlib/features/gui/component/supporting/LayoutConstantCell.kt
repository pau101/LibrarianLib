package com.teamwizardry.librarianlib.features.gui.component.supporting

class LayoutConstantCell(value: Number){
    var value: Number = value
        set(value) {
            field = value
            changeHandlers.forEach { it() }
        }

    private val changeHandlers = mutableListOf<() -> Unit>()
    fun onChange(handler: () -> Unit) {
        changeHandlers.add(handler)
    }

    /**
     * Return an anchor multiplied by [other]
     */
    operator fun times(other: LayoutExpression): LayoutExpression = LayoutExpressionTimes(LayoutExpressionMutableConstant(this), other)

    /**
     * Return an anchor divided by [other]
     */
    operator fun div(other: LayoutExpression): LayoutExpression = LayoutExpressionDiv(LayoutExpressionMutableConstant(this), other)

    /**
     * Return an anchor increased by [other]
     */
    operator fun plus(other: LayoutExpression): LayoutExpression  = LayoutExpressionPlus(LayoutExpressionMutableConstant(this), other)

    /**
     * Return an anchor reduced by [other]
     */
    operator fun minus(other: LayoutExpression): LayoutExpression  = LayoutExpressionMinus(LayoutExpressionMutableConstant(this), other)

    operator fun plusAssign(other: Number) { value = value.toDouble() + other.toDouble() }
    operator fun minusAssign(other: Number) { value = value.toDouble() - other.toDouble() }
    operator fun timesAssign(other: Number) { value = value.toDouble() * other.toDouble() }
    operator fun divAssign(other: Number) { value = value.toDouble() / other.toDouble() }
}