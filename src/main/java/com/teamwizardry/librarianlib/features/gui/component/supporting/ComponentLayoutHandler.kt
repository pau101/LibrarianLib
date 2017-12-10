package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import no.birkett.kiwi.*

@Suppress("LEAKING_THIS", "UNUSED")
class ComponentLayoutHandler(val component: GuiComponent) {

    private val posX = Anchor(component, "posX")
    private val posY = Anchor(component, "posY")
    private val sizeX = Anchor(component, "width")
    private val sizeY = Anchor(component, "height")

    /** The anchor corresponding to the minimum x coordinate of the component's bounds */
    val left: LayoutExpression
        get() {
            val parentLayout = component.parent?.layout ?: return posX
            if(parentLayout.solver != null) return posX
            return named(parentLayout.left + posX, "left")
        }
    /** The anchor corresponding to the minimum y coordinate of the component's bounds */
    val top: LayoutExpression
        get() {
            val parentLayout = component.parent?.layout ?: return posY
            if(parentLayout.solver != null) return posY
            return named(parentLayout.top + posY, "left")
        }
    /** The anchor corresponding to the maximum x coordinate of the component's bounds */
    val right: LayoutExpression
        get() = named(left + width, "right")
    /** The anchor corresponding to the maximum y coordinate of the component's bounds */
    val bottom: LayoutExpression
        get() = named(top + height, "bottom")
    /** The anchor corresponding to the x coordinate of the center of the component's bounds */
    val centerX: LayoutExpression
        get() = named((left + right) / 2, "centerX")
    /** The anchor corresponding to the y coordinate of the center of the component's bounds */
    val centerY: LayoutExpression
        get() = named((top + bottom) / 2, "centerY")

    /** The anchor corresponding to the width of the component's bounds */
    val width: LayoutExpression
        get() = sizeX
    /** The anchor corresponding to the height of the component's bounds */
    val height: LayoutExpression
        get() = sizeY

    /**
     * If nonzero, the width and height constraints will be set to the component's implicit size if it exists, rather
     * than the component's size attribute, and use this strength
     *
     * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED]
     */
    var implicitSizeStrength = Strength.IMPLICIT

    /**
     * Calls the passed lambda when adding constraints. The lambda may be called multiple times if the component is
     * removed from its parent and added to another parent.
     */
    fun constraints(lambda: Runnable) {
        constraintCallbacks.add(lambda)

        if(containingSolver != null) {
            lambda.run()
        }
    }

    fun add(constraint: LayoutConstraint) {
        containingSolver?.addConstraint(constraint.kiwiConstraint)
    }

    fun remove(constraint: LayoutConstraint) {
        containingSolver?.removeConstraint(constraint.kiwiConstraint)
    }

    /** Specifies the strength with which the x coordinate defined by [GuiComponent.pos] should be maintained
     * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var leftStay = 0.0
    /** Specifies the strength with which the y coordinate defined by [GuiComponent.pos] should be maintained
    * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var topStay = 0.0
    /** Specifies the strength with which the width defined by [GuiComponent.size] should be maintained
    * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var widthStay = 0.0
    /** Specifies the strength with which the height defined by [GuiComponent.size] should be maintained
    * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var heightStay = 0.0

    /** Specifies the strength with which the size defined by [GuiComponent.size] should be maintained
    * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var sizeStay
        get() = Math.min(widthStay, heightStay)
        set(value) {
            widthStay = value
            heightStay = value
        }

    /** Specifies the strength with which the position defined by [GuiComponent.pos] should be maintained
    * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var positionStay
        get() = Math.min(leftStay, topStay)
        set(value) {
            leftStay = value
            topStay = value
        }

    /** Specifies the strength with which the size and position defined by [GuiComponent.size] and [GuiComponent.pos] should be maintained
    * If set to [Strength.REQUIRED] or higher, this will be clamped slightly lower than [Strength.REQUIRED] */
    var boundsStay
        get() = Math.min(sizeStay, positionStay)
        set(value) {
            sizeStay = value
            positionStay = value
        }

    /**
     * Creates constraints to attach this component's top, left, bottom, and right anchors to the same anchors on
     * [other], adding the respective values in [topLeftPlus] and [bottomRightPlus] to [other]'s constraints.
     *
     * Adds the resulting constraints to this component.
     *
     * @return An array of the constraints added in the order `left, top, right, bottom`
     */
    @JvmOverloads
    fun boundsEqualTo(other: GuiComponent, topLeftPlus: Vec2d = Vec2d.ZERO, bottomRightPlus: Vec2d = Vec2d.ZERO): Array<LayoutConstraint> {
        return arrayOf(
                left.equalTo(other.layout.left + topLeftPlus.x),
                top.equalTo(other.layout.top + topLeftPlus.y),
                right.equalTo(other.layout.right + bottomRightPlus.x),
                bottom.equalTo(other.layout.bottom + bottomRightPlus.y)
        )
    }

    /**
     * Creates constraints to attach this component's top and left anchors to the same anchors on
     * [other], adding the respective values in [plus] to [other]'s constraints.
     *
     * Adds the resulting constraints to this component.
     *
     * @return An array of the constraints added in the order `left, top`
     */
    @JvmOverloads
    fun posEqualTo(other: GuiComponent, plus: Vec2d = Vec2d.ZERO): Array<LayoutConstraint> {
        return arrayOf(
                left.equalTo(other.layout.left + plus.x),
                top.equalTo(other.layout.top + plus.y)
        )
    }

    /**
     * Creates constraints to attach this component's width and height anchors to the same anchors on
     * [other], adding the respective values in [plus] to [other]'s constraints.
     *
     * Adds the resulting constraints to this component.
     *
     * @return An array of the constraints added in the order `width, height`
     */
    @JvmOverloads
    fun sizeEqualTo(other: GuiComponent, plus: Vec2d = Vec2d.ZERO): Array<LayoutConstraint> {
        return arrayOf(
                width.equalTo(other.layout.width + plus.x),
                height.equalTo(other.layout.height + plus.y)
        )
    }

    /**
     * Isolates
     */
    fun isolate() {
        if(component.relationships.components.isNotEmpty()) {
            throw IllegalStateException("Cannot isolate a component once it has children!")
        }
        if(component.relationships.parent != null) {
            throw IllegalStateException("Cannot isolate a component once it has been added to a component tree!")
        }
        val solver = Solver()
        this.solver = solver
        isolated = true

        setNeedsLayout()
    }

    fun bake() {
        if(!isolated) {
            throw IllegalStateException("Only isolated layouts can be baked")
        }
        if(baked) {
            throw IllegalStateException("Component already baked")
        }
        needsLayout = true
        updateLayoutIfNeeded()
        solver = null
        baked = true
        component.layout {
            sizeX eq component.size.x
            sizeY eq component.size.y
        }
    }

    fun setNeedsLayout() {
        containingSolverComponent?.also {
            it.layout.needsLayout = true
        }
    }

    fun updateLayoutIfNeeded() {
        val solver = this.solver
        if(solver == null) {
            containingSolverComponent?.layout?.updateLayoutIfNeeded()
        } else if (needsLayout || solver.changed) {
            addIntrinsic(solver)
            solver.updateVariables()
            update(solver)

            needsLayout = false
            solver.changed = false
        }
    }

    //region internals

    var isolated = false
        private set
    private val constraintCallbacks = mutableListOf<Runnable>()
    private var needsLayout = false
    private var baked = false

    internal var solver: Solver? = null
        set(value) {
            if(baked) throw IllegalStateException("Component already baked!")
            field = value
            if(value != null) {
                value.addConstraint(Symbolics.equals(posX.variable, 0.0))
                value.addConstraint(Symbolics.equals(posY.variable, 0.0))
            }
            onAddedToLayoutContext()
        }

    internal val containingSolver: Solver?
        get() = component.parent?.layout?.solver ?: component.parent?.layout?.containingSolver
    internal val validSolvers: List<Solver>
        get() = listOf(solver, containingSolver).filterNotNull()
    internal val containingSolverComponent: GuiComponent?
        get() = if(component.parent?.layout?.solver != null) component.parent else component.parent?.layout?.containingSolverComponent

    private fun addIntrinsic(solver: Solver) {
        if(isolated) {
            if(solver == this.solver) {
                val implicit = component.getImplicitSize()
                if (implicitSizeStrength != 0.0 && implicit != null) {
                    solver.setEditVariable(sizeX.variable, implicit.x, implicitSizeStrength, "width")
                    solver.setEditVariable(sizeY.variable, implicit.y, implicitSizeStrength, "height")
                } else {
                    solver.setEditVariable(sizeX.variable, component.size.x, widthStay, "width")
                    solver.setEditVariable(sizeY.variable, component.size.y, heightStay, "height")
                }
            } else {
                solver.setEditVariable(sizeX.variable, component.size.x, Strength.REQUIRED, "width")
                solver.setEditVariable(sizeY.variable, component.size.y, Strength.REQUIRED, "height")

                solver.setEditVariable(posX.variable, component.pos.x, leftStay, "posX")
                solver.setEditVariable(posY.variable, component.pos.y, topStay, "posY")
            }
        } else {
            val implicit = component.getImplicitSize()
            if (implicitSizeStrength != 0.0 && implicit != null) {
                solver.setEditVariable(sizeX.variable, implicit.x, implicitSizeStrength, "width")
                solver.setEditVariable(sizeY.variable, implicit.y, implicitSizeStrength, "height")
            } else {
                solver.setEditVariable(sizeX.variable, component.size.x, widthStay, "width")
                solver.setEditVariable(sizeY.variable, component.size.y, heightStay, "height")
            }

            solver.setEditVariable(posX.variable, component.pos.x, leftStay, "posX")
            solver.setEditVariable(posY.variable, component.pos.y, topStay, "posY")
        }
        component.relationships.components.forEach {
            it.layout.addIntrinsic(solver)
        }
    }

    private fun update(solver: Solver) {
        if(solver != this.solver)
            component.transform.translate = vec(posX.variable.value, posY.variable.value)
        component.size = vec(sizeX.variable.value, sizeY.variable.value)

        component.relationships.components.forEach {
            it.layout.update(solver)
        }
    }

    init {
        component.BUS.hook(GuiComponentEvents.PostAddToParentEvent::class.java) { e ->
            if(solver == null && containingSolver != null)
                onAddedToLayoutContext()
        }
    }

    private fun onAddedToLayoutContext() {
        constraintCallbacks.forEach(Runnable::run)
        component.relationships.components.forEach {
            if(it.layout.solver != null) return@forEach
            it.layout.onAddedToLayoutContext()
        }
    }

    // Uses depth-first search so subcomponents with isolated layouts are updated first
    internal fun updateAllLayoutsIfNeeded() {
        component.relationships.components.forEach {
            it.layout.updateAllLayoutsIfNeeded()
        }
        if(solver != null)
            updateLayoutIfNeeded()
    }

    fun named(expr: LayoutExpression, name: String): LayoutExpression {
        expr.stringRepresentation =
                component.relationships.guiPath() + "@" + System.identityHashCode(component).toString(16) + "#" + name
        return expr
    }

    /**
     * Called for every constraint added to this root, this method replaces this component's `left` and `right` anchors
     * with 0
     */
    internal fun adjustChildConstraint(constraint: Constraint): Constraint {
        val toRemove = left.kiwiExpression.terms.map { it.variable } +
                top.kiwiExpression.terms.map { it.variable }

        constraint.expression.terms.removeIf { it.variable in toRemove }

        return constraint
    }

    internal fun Solver.setEditVariable(variable: Variable, value: Double, strength: Double, name: String) {
//        val strength = if(strength >= Strength.REQUIRED) Strength.REQUIRED - 1 else strength

        var edit = editVariable(variable)

        if(edit != null && strength == 0.0) {
            removeEditVariable(variable)
            return
        }

        if(edit?.constraint?.strength != strength) {
            if(edit != null) removeEditVariable(variable)
            try {
                addEditVariable(variable, strength, value)
            } catch (e: UnsatisfiableConstraintException) {
                val fullName =
                        component.relationships.guiPath() + "@" + System.identityHashCode(component).toString(16) + "#" + name
                LibrarianLog.error("Unsatisfiable edit variable: $fullName == $value strength: ${Strength.name(strength)}")
                LibrarianLog.errorStackTrace(e)
            }
        }
        edit = editVariable(variable)
        if(edit?.constant != value) {
            suggestValue(variable, value)
        }
    }
    //endregion
}

