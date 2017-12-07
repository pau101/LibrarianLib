package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.google.common.collect.Sets
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import no.birkett.kiwi.*
import java.util.*

@Suppress("LEAKING_THIS", "UNUSED")
class ComponentLayoutHandler(val component: GuiComponent) {

    @JvmField val posX = Anchor(component, "posX")
    @JvmField val posY = Anchor(component, "posY")
    @JvmField val sizeX = Anchor(component, "sizeX")
    @JvmField val sizeY = Anchor(component, "sizeY")

    /** The anchor corresponding to the minimum x coordinate of the component's bounds */
    val left: LayoutExpression
        get() = component.parent?.layout?.left?.let { it + posX } ?: posX
    /** The anchor corresponding to the maximum x coordinate of the component's bounds */
    val right: LayoutExpression
        get() = left + width
    /** The anchor corresponding to the minimum y coordinate of the component's bounds */
    val top: LayoutExpression
        get() = component.parent?.layout?.top?.let { it + posY } ?: posY
    /** The anchor corresponding to the maximum y coordinate of the component's bounds */
    val bottom: LayoutExpression
        get() = top + height
    /** The anchor corresponding to the x coordinate of the center of the component's bounds */
    val centerX: LayoutExpression
        get() = (left + right) / 2
    /** The anchor corresponding to the y coordinate of the center of the component's bounds */
    val centerY: LayoutExpression
        get() = (top + bottom) / 2

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

        if(rootSolver != null) {
            lambda.run()
        }
    }

    fun add(constraint: LayoutConstraint) {
        rootSolver?.addConstraint(constraint.kiwiConstraint)
    }

    fun remove(constraint: LayoutConstraint) {
        rootSolver?.removeConstraint(constraint.kiwiConstraint)
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

    var isolated = false
        private set

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
        rootSolver = Solver()
        isolated = true
    }

    fun bake() {
        if(!isolated) {
            throw IllegalStateException("Only isolated layouts can be baked!")
        }
        setNeedsLayout()
        updateLayoutIfNeeded()
        baked = true
        solverStorage = null
    }

    fun setNeedsLayout() {
        solverRootComponent?.also {
            it.layout.needsLayout = true
        }
    }

    fun updateLayoutIfNeeded() {
        val solver = this.solverStorage
        if(solver == null) {
            solverRootComponent?.layout?.updateLayoutIfNeeded()
        } else if (needsLayout || solver.changed) {
            addIntrinsic()
            solver.updateVariables()
            update()

            needsLayout = false
            solver.changed = false
        }
    }

    //region internals

    private val constraintCallbacks = mutableListOf<Runnable>()

    private var needsLayout = false
    private var solverStorage: Solver? = null
    internal var rootSolver: Solver?
        get() = if(baked) null else solverStorage ?: component.parent?.layout?.rootSolver
        set(value) {
            if(baked) throw IllegalStateException("Component already baked!")
            solverStorage = value
            onAddedToLayoutContext()
        }
    internal val solverRootComponent: GuiComponent?
        get() = if(baked) null else if(solverStorage != null) this.component else this.component.parent?.layout?.solverRootComponent
    private var baked = false

    private fun addIntrinsic() {
        val implicit = component.getImplicitSize()
        if(implicitSizeStrength != 0.0 && implicit != null) {
            rootSolver?.setEditVariable(sizeX.variable, implicit.x, implicitSizeStrength)
            rootSolver?.setEditVariable(sizeY.variable, implicit.y, implicitSizeStrength)
        } else {
            rootSolver?.setEditVariable(sizeX.variable, component.size.x, widthStay)
            rootSolver?.setEditVariable(sizeY.variable, component.size.y, heightStay)

        }
        rootSolver?.setEditVariable(posX.variable, component.pos.x, leftStay)
        rootSolver?.setEditVariable(posY.variable, component.pos.y, topStay)

        component.relationships.components.forEach {
            it.layout.addIntrinsic()
        }
    }

    private fun update() {
        component.transform.translate = vec(posX.variable.value, posY.variable.value)
        component.size = vec(sizeX.variable.value, sizeY.variable.value)

        component.relationships.components.forEach {
            it.layout.update()
        }
    }

    init {
        component.BUS.hook(GuiComponentEvents.PostAddToParentEvent::class.java) { e ->
            if(solverStorage == null && rootSolver != null)
                onAddedToLayoutContext()
        }
    }

    private fun onAddedToLayoutContext() {
        constraintCallbacks.forEach(Runnable::run)
        component.relationships.components.forEach {
            if(it.layout.solverStorage != null) return@forEach
            it.layout.onAddedToLayoutContext()
        }
    }

    // Uses depth-first search so subcomponents with isolated layouts are updated first
    internal fun updateAllLayoutsIfNeeded() {
        component.relationships.components.forEach {
            it.layout.updateAllLayoutsIfNeeded()
        }
        if(solverStorage != null)
            updateLayoutIfNeeded()
    }
    //endregion
}

fun Solver.setEditVariable(variable: Variable, value: Double, strength: Double) {
    val strength = if(strength >= Strength.REQUIRED) Strength.REQUIRED - 1 else strength

    val edit = editVariable(variable)
    if(edit?.constraint?.strength != strength) {
        if(hasEditVariable(variable)) removeEditVariable(variable)
        addEditVariable(variable, strength)
    }
    if(edit?.constant != value) {
        suggestValue(variable, value)
    }
}
