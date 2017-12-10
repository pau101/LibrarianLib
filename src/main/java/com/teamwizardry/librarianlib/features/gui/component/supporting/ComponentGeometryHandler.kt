package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.round
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * TODO: Document file ComponentGeometryHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentGeometryHandler(private val component: GuiComponent) {
    /** [GuiComponent.transform] */
    val transform = ComponentTransform(component)
    var size: Vec2d = vec(0, 0)
        internal set(value) {
            val v = if(integerBounds) value.round() else value
            if(field != v) {
                component.layout.setNeedsLayout()
            }
            field = v
        }
    var pos: Vec2d
        get() = transform.translate
        internal set(value) {
            val v = if(integerBounds) value.round() else value
            if(transform.translate != v) {
                component.layout.setNeedsLayout()
            }
            transform.translate = v
        }
    /** Set to true to snap all position and size values to integers */
    var integerBounds = false
//    /** [GuiComponent.pos] */
//    var pos: Vec2d
//        get() = transform.translate
//        set(value) { transform.translate = value }
    /** [GuiComponent.mouseOver] */
    var mouseOver = false
    /** True if the mouse is over the component at all, ignoring other components on top of it blocking the cursor */
    var mouseOverNoOcclusion = false
    /** True if the mouse is directly over this component and not one of its children */
    var mouseOverDirectly = false

    /**
     * Set whether the element should calculate hovering based on it's bounds as
     * well as it's children or if it should only calculate based on it's children.
     */
    var shouldCalculateOwnHover = true

    /**
     * Multiplies this component's scale by all of its parents' scales.
     */
    fun getScaleFactor(): Vec2d {
        return this.transform.scale2D * (component.parent?.transform?.scale2D ?: vec(1,1))
    }

    /**
     * Takes [pos], which is in our parent's context (coordinate space), and transforms it to our context
     */
    fun transformFromParentContext(pos: Vec2d): Vec2d {
        return transform.applyInverse(pos)
    }

    /** [GuiComponent.mouseOver] */
    @JvmOverloads
    fun transformToParentContext(pos: Vec2d = Vec2d.ZERO): Vec2d {
        return transform.apply(pos)
    }

    /**
     * Create a matrix that moves coordinates from [other]'s context (coordinate space) to this component's context
     *
     * If [other] is null the returned matrix moves coordinates from the root context to this component's context
     */
    fun otherContextToThisContext(other: GuiComponent?): Matrix4 {
        if(other == null)
            return thisContextToOtherContext(null).invert()
        return other.geometry.thisContextToOtherContext(component)
    }

    /**
     * Create a matrix that moves coordinates from this component's context (coordinate space) to [other]'s context
     *
     * If [other] is null the returned matrix moves coordinates from this component's context to the root context
     */
    fun thisContextToOtherContext(other: GuiComponent?): Matrix4 {
        return _thisContextToOtherContext(other, Matrix4())
    }
    private fun _thisContextToOtherContext(other: GuiComponent?, matrix: Matrix4): Matrix4 {
        if(other == null) {
            component.parent?.geometry?._thisContextToOtherContext(null, matrix)
            transform.apply(matrix)
            return matrix
        }
        val mat = other.geometry.thisContextToOtherContext(null).invert()
        mat *= thisContextToOtherContext(null)
        return mat
    }

    fun thisPosToOtherContext(other: GuiComponent?, pos: Vec2d): Vec2d {
        return thisContextToOtherContext(other) * pos
    }

    fun otherPosToThisContext(other: GuiComponent?, pos: Vec2d): Vec2d {
        return otherContextToThisContext(other) * pos
    }

    fun calculateMouseOver(mousePos: Vec2d) {
        component.BUS.fire(GuiComponentEvents.PreMouseOverEvent(component, mousePos))
        val mousePos = transformFromParentContext(mousePos)
        this.mouseOver = false

        if (component.isVisible) {

            var mouseOverChild = false
            component.relationships.components.asReversed().forEach { child ->
                child.geometry.calculateMouseOver(mousePos)
                if (mouseOverChild) {
                    child.mouseOver = false // occlusion
                }
                if (child.mouseOver) {
                    mouseOverChild = true
                    mouseOver = true // mouseover upward transfer
                }
            }

            mouseOver = mouseOver || (shouldCalculateOwnHover && calculateOwnHover(mousePos))
            mouseOver = component.BUS.fire(GuiComponentEvents.MouseOverEvent(component, mousePos, mouseOver)).isOver
            mouseOverNoOcclusion = mouseOver
            mouseOverDirectly = mouseOver && !mouseOverChild
        }
    }

    internal fun allMouseOversFalse() {
        this.mouseOver = false
        component.relationships.components.forEach { it.geometry.allMouseOversFalse() }
    }

    /**
     * Override this to change the shape of a hover. For instance making a per-pixel sprite hover
     */
    open fun calculateOwnHover(mousePos: Vec2d): Boolean {
        return mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y
    }

}
