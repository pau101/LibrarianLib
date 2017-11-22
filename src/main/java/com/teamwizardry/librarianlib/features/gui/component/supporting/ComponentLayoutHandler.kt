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
    /** The anchor corresponding to the minimum x coordinate of the component's bounds */
    @JvmField val left = Anchor(component, Vec2d.Axis.X)
    /** The anchor corresponding to the maximum x coordinate of the component's bounds */
    @JvmField val right = Anchor(component, Vec2d.Axis.X)
    /** The anchor corresponding to the minimum y coordinate of the component's bounds */
    @JvmField val top = Anchor(component, Vec2d.Axis.Y)
    /** The anchor corresponding to the maximum y coordinate of the component's bounds */
    @JvmField val bottom = Anchor(component, Vec2d.Axis.Y)
    /** The anchor corresponding to the x coordinate of the center of the component's bounds */
    @JvmField val centerX = Anchor(component, Vec2d.Axis.X)
    /** The anchor corresponding to the y coordinate of the center of the component's bounds */
    @JvmField val centerY = Anchor(component, Vec2d.Axis.Y)

    /** The anchor corresponding to the width of the component's bounds */
    @JvmField val width = Anchor(component, Vec2d.Axis.X)
    /** The anchor corresponding to the height of the component's bounds */
    @JvmField val height = Anchor(component, Vec2d.Axis.Y)

    /** An anchor always set to zero on the X axis */
    @JvmField val zeroX = Anchor(component, Vec2d.Axis.X)
    /** An anchor always set to zero on the Y axis */
    @JvmField val zeroY = Anchor(component, Vec2d.Axis.Y)
    /**
     * If set to true, the width and height constraints will be set to the component's implicit size if it exists, rather
     * than the component's size attribute
     */
    var useImplicitSize = true

    init {
        width.strength = Strength.MEDIUM
        height.strength = Strength.MEDIUM

        width.relativeVariable = width.variable
        height.relativeVariable = height.variable
    }

    private val constraints = Sets.newSetFromMap(IdentityHashMap<LayoutConstraint, Boolean>())

    fun add(constraint: LayoutConstraint) {
        constraints.add(constraint)
    }

    fun remove(constraint: LayoutConstraint) {
        constraints.remove(constraint)
    }

    /**
     * Makes the size as defined by [GuiComponent.size] fixed by setting its constraint to be [Strength.REQUIRED]
     */
    fun fixedSize() {
        width.strength = Strength.REQUIRED
        height.strength = Strength.REQUIRED
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

    internal fun addBase(solver: Solver, parentScaleFactor: Vec2d, parentLeft: Variable?, parentTop: Variable?) {
        val name = component.relationships.guiPath()
        left.setName(name + "#left")
        right.setName(name + "#right")
        top.setName(name + "#top")
        bottom.setName(name + "#bottom")
        width.setName(name + "#width")
        height.setName(name + "#height")
        centerX.setName(name + "#centerX")
        centerY.setName(name + "#centerY")

        val implicitSize = component.getImplicitSize()
        val scaleFactor = parentScaleFactor * component.transform.scale2D

        solver.addConstraint(Symbolics.equals(zeroX.variable, 0.0).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.equals(zeroY.variable, 0.0).setStrength(Strength.REQUIRED))

        // absolute pos given parent pos and relative pos
        if(parentLeft == null) {
            solver.addConstraint(Symbolics.equals(left.variable, component.pos.x).setStrength(Strength.REQUIRED))
        } else {
            solver.addConstraint(Symbolics.equals(left.relativeVariable, component.pos.x * parentScaleFactor.x).setStrength(left.strength))
            solver.addConstraint(Symbolics.equals(left.variable, Symbolics.add(parentLeft, left.relativeVariable)).setStrength(Strength.REQUIRED))
        }
        if(parentTop == null) {
            solver.addConstraint(Symbolics.equals(top.variable, component.pos.y).setStrength(Strength.REQUIRED))
        } else {
            solver.addConstraint(Symbolics.equals(top.relativeVariable, component.pos.y * parentScaleFactor.y).setStrength(top.strength))
            solver.addConstraint(Symbolics.equals(top.variable, Symbolics.add(parentTop, top.relativeVariable)).setStrength(Strength.REQUIRED))
        }

        if(useImplicitSize && implicitSize != null) {
            // implicit width and height in global coord space
            solver.addConstraint(Symbolics.equals(width.variable, implicitSize.x * scaleFactor.x).setStrength(Strength.IMPLICIT))
            solver.addConstraint(Symbolics.equals(height.variable, implicitSize.y * scaleFactor.y).setStrength(Strength.IMPLICIT))
        } else {
            // width and height in global coord space
            solver.addConstraint(Symbolics.equals(width.variable, component.size.x * scaleFactor.x).setStrength(width.strength))
            solver.addConstraint(Symbolics.equals(height.variable, component.size.y * scaleFactor.y).setStrength(height.strength))
        }

        // limit width and height to be >= 0
        solver.addConstraint(Symbolics.greaterThanOrEqualTo(width.variable, 0.0).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.greaterThanOrEqualTo(height.variable, 0.0).setStrength(Strength.REQUIRED))

        // right and bottom based on top, left, width, and height
        solver.addConstraint(Symbolics.equals(right.variable, Symbolics.add(left.variable, width.variable)).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.equals(bottom.variable, Symbolics.add(top.variable, height.variable)).setStrength(Strength.REQUIRED))

        // right and bottom relative to parent, scaled to global coord space
        solver.addConstraint(Symbolics.equals(right.relativeVariable, Symbolics.add(left.relativeVariable, width.variable)).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.equals(bottom.relativeVariable, Symbolics.add(top.relativeVariable, height.variable)).setStrength(Strength.REQUIRED))

        // centerX/Y based on left and right
        solver.addConstraint(Symbolics.equals(centerX.variable, Symbolics.divide(Symbolics.add(left.variable, right.variable), 2.0)).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.equals(centerY.variable, Symbolics.divide(Symbolics.add(top.variable, bottom.variable), 2.0)).setStrength(Strength.REQUIRED))

        // centerX/Y relative to parent, scaled to global coord space
        if(parentLeft != null)
            solver.addConstraint(Symbolics.equals(centerX.relativeVariable, Symbolics.subtract(centerX.variable, parentLeft)).setStrength(Strength.REQUIRED))
        else
            solver.addConstraint(Symbolics.equals(centerX.relativeVariable, Symbolics.subtract(centerX.variable, 0.0)).setStrength(Strength.REQUIRED))
        if(parentTop != null)
            solver.addConstraint(Symbolics.equals(centerY.relativeVariable, Symbolics.subtract(centerY.variable, parentTop)).setStrength(Strength.REQUIRED))
        else
            solver.addConstraint(Symbolics.equals(centerY.relativeVariable, Symbolics.subtract(centerY.variable, 0.0)).setStrength(Strength.REQUIRED))

        component.relationships.children.forEach {
            it.layout.addBase(solver, scaleFactor, left.variable, top.variable)
        }
    }

    internal fun addCustom(solver: Solver) {
        component.BUS.fire(GuiComponentEvents.AddConstraintsEvent(component, solver))
        constraints.forEach {
            solver.addConstraint(it.makeConstraint())
        }
        component.relationships.children.forEach {
            it.layout.addCustom(solver)
        }
    }

    internal fun update() {
        val rootMin = vec(left.variable.value, top.variable.value)
        val rootMax = vec(right.variable.value, bottom.variable.value)

        val parent = component.relationships.parent
        if(parent != null) {
            val pos = parent.otherPosToThisContext(null, rootMin)
            component.pos = pos

            val size = component.otherPosToThisContext(null, rootMax)
            component.size = size
        }

        component.relationships.children.forEach {
            it.layout.update()
        }
    }

}