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

    operator fun <T: ExpressionMetric> times(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionTimes(LayoutExpressionMutableConstant(this), other)
    operator fun <T: ExpressionMetric> div(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionDiv(LayoutExpressionMutableConstant(this), other)
    operator fun <T: ExpressionMetric> plus(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionPlus(LayoutExpressionMutableConstant(this), other)
    operator fun <T: ExpressionMetric> minus(other: LayoutExpression<T>): LayoutExpression<T> = LayoutExpressionMinus(LayoutExpressionMutableConstant(this), other)

    operator fun plusAssign(other: Number) { value = value.toDouble() + other.toDouble() }
    operator fun minusAssign(other: Number) { value = value.toDouble() - other.toDouble() }
    operator fun timesAssign(other: Number) { value = value.toDouble() * other.toDouble() }
    operator fun divAssign(other: Number) { value = value.toDouble() / other.toDouble() }
}