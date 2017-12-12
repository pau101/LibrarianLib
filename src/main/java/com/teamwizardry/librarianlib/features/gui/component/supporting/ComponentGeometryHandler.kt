package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
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
    /** Set to true to snap the position and size to integer coordinates */
    var integerBounds = false
//    /** [GuiComponent.pos] */
//    var pos: Vec2d
//        get() = transform.translate
//        set(value) { transform.translate = value }
    var mousePos = Vec2d.ZERO
        internal set
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
        this.mousePos = mousePos
        this.mouseOver = false

        if (component.isVisible) {
            var overSelf = isMouseOverSelf(mousePos)

            var mouseOverChild = false
            if(!component.clipping.clipToBounds || overSelf) {
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
            }

            mouseOver = mouseOver || (shouldCalculateOwnHover && overSelf)
            mouseOver = component.BUS.fire(GuiComponentEvents.MouseOverEvent(component, mousePos, mouseOver)).isOver
            mouseOverNoOcclusion = mouseOver
            mouseOverDirectly = mouseOver && !mouseOverChild
        }
    }

    internal fun allMouseOversFalse() {
        this.mouseOver = false
        component.relationships.components.forEach { it.geometry.allMouseOversFalse() }
    }

    internal fun isMouseOverSelf(mousePos: Vec2d): Boolean {
        val r = component.clipping.cornerRadius

        if(isInRect(mousePos, 0.0, r, size.x, size.y-r)) return true
        if(isInRect(mousePos, r, 0.0, size.x-r, r)) return true
        if(isInRect(mousePos, r, size.y-r, size.x-r, size.y)) return true

        if(r > 0) {
            if (component.clipping.cornerPixelSize <= 0) {
                val distSq = component.clipping.cornerPixelSize * component.clipping.cornerPixelSize
                if(vec(       r,        r).squareDist(mousePos) < distSq) return true
                if(vec(       r, size.y-r).squareDist(mousePos) < distSq) return true
                if(vec(size.x-r, size.y-r).squareDist(mousePos) < distSq) return true
                if(vec(       r, size.y-r).squareDist(mousePos) < distSq) return true
            } else {
                if(isInPixelatedArc(mousePos,        r,        r)) return true
                if(isInPixelatedArc(mousePos,        r, size.y-r)) return true
                if(isInPixelatedArc(mousePos, size.x-r, size.y-r)) return true
                if(isInPixelatedArc(mousePos, size.x-r,        r)) return true
            }
        }

        return false
    }

    private fun isInRect(pos: Vec2d, minX: Double, minY: Double, maxX: Double, maxY: Double): Boolean {
        return pos.x in minX..maxX && pos.y in minY..maxY
    }

    private fun isInPixelatedArc(mousePos: Vec2d, x: Double, y: Double): Boolean {
        val v = vec(Math.abs(mousePos.x - x), Math.abs(mousePos.y - y))
        val a = vec(component.clipping.cornerPixelSize, 0)
        val b = vec(0, component.clipping.cornerPixelSize)
        val r = component.clipping.cornerRadius / component.clipping.cornerPixelSize
        var x = 0
        var y = r.toInt()
        var d: Int

        val points = mutableMapOf<Int, Int>()

        points[x] = y
        d = 3 - 2 * r.toInt()
        while (x <= y) {
            if (d <= 0) {
                d = d + (4 * x + 6)
            } else {
                d = d + 4 * (x - y) + 10
                y--
            }
            x++

            if(x-1 > 0)
                points[x-1] = Math.max(points[x] ?: 0, y)
            if(y-1 > 0)
                points[y-1] = Math.max(points[y] ?: 0, x)
        }

        points.forEach { (y, x) ->
            if(isInRect(v, 0.0, y.toDouble(), x.toDouble(), y+1.0)) return@isInPixelatedArc true
        }

        return false
    }

}
