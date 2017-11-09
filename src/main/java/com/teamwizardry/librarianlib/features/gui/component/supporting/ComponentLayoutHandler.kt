package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.google.common.collect.Sets
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import no.birkett.kiwi.*
import java.util.*

@Suppress("LEAKING_THIS", "UNUSED")
class ComponentLayoutHandler(val component: GuiComponent) {
    /** Whether this component should be positioned with autolayout */
    var enabled = false

    /** The anchor corresponding to the minimum x coordinate of the component's bounds */
    val left = Anchor(this)
    /** The anchor corresponding to the maximum x coordinate of the component's bounds */
    val right = Anchor(this)
    /** The anchor corresponding to the minimum y coordinate of the component's bounds */
    val top = Anchor(this)
    /** The anchor corresponding to the maximum y coordinate of the component's bounds */
    val bottom = Anchor(this)
    /** The anchor corresponding to the center x coordinate of the component's bounds */
    val centerX = Anchor(this)
    /** The anchor corresponding to the center y coordinate of the component's bounds */
    val centerY = Anchor(this)

    /** The anchor corresponding to the width of the component's bounds */
    val width = Anchor(this)
    /** The anchor corresponding to the height of the component's bounds */
    val height = Anchor(this)
    init {
        width.strength = Strength.MEDIUM
        height.strength = Strength.MEDIUM
    }

    private var flipX = false
    private var flipY = false

    private val constraints = Sets.newSetFromMap(IdentityHashMap<Constraint, Boolean>())

    fun add(constraint: Constraint) {
        constraints.add(constraint)
    }

    fun remove(constraint: Constraint) {
        constraints.remove(constraint)
    }

    internal fun addTo(solver: Solver) {
        var rootMin = component.thisPosToOtherContext(null, vec(0, 0))
        var rootMax = component.thisPosToOtherContext(null, component.geometry.size)

        if (rootMin.x > rootMax.x)
            flipX = true
        if (rootMin.y > rootMax.y)
            flipY = true
        rootMin = vec(
                if (flipX) rootMax.x else rootMin.x,
                if (flipY) rootMax.y else rootMin.y
        )
        rootMax = vec(
                if (flipX) rootMin.x else rootMax.x,
                if (flipY) rootMin.y else rootMax.y
        )

        solver.addConstraint(Symbolics.equals(left, rootMin.x).setStrength(left.strength))
        solver.addConstraint(Symbolics.equals(top, rootMin.y).setStrength(top.strength))

        solver.addConstraint(Symbolics.equals(right, rootMax.x).setStrength(right.strength))
        solver.addConstraint(Symbolics.equals(bottom, rootMax.y).setStrength(bottom.strength))

        solver.addConstraint(Symbolics.equals(width, rootMax.x - rootMin.x).setStrength(width.strength))
        solver.addConstraint(Symbolics.equals(height, rootMax.y - rootMin.y).setStrength(height.strength))

        solver.addConstraint(Symbolics.equals(centerX, (rootMax.x + rootMin.x) / 2).setStrength(centerX.strength))
        solver.addConstraint(Symbolics.equals(centerY, (rootMax.y + rootMin.y) / 2).setStrength(centerY.strength))

        solver.addConstraint(Symbolics.greaterThanOrEqualTo(width, 0.0).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.greaterThanOrEqualTo(height, 0.0).setStrength(Strength.REQUIRED))

        solver.addConstraint(Symbolics.equals(width, Symbolics.subtract(right, left)).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.equals(height, Symbolics.subtract(bottom, top)).setStrength(Strength.REQUIRED))

        solver.addConstraint(Symbolics.equals(centerX, Symbolics.divide(Symbolics.add(left, right), 2.0)).setStrength(Strength.REQUIRED))
        solver.addConstraint(Symbolics.equals(centerY, Symbolics.divide(Symbolics.add(top, bottom), 2.0)).setStrength(Strength.REQUIRED))

        component.BUS.fire(GuiComponentEvents.AddConstraintsEvent(component, solver))
        constraints.forEach {
            solver.addConstraint(it)
        }
        component.relationships.children.forEach {
            it.layout.addTo(solver)
        }
    }

    internal fun update() {
        var rootMin = vec(left.value, top.value)
        var rootMax = vec(right.value, bottom.value)

        rootMin = vec(
                if(flipX) rootMax.x else rootMin.x,
                if(flipY) rootMax.y else rootMin.y
        )
        rootMax = vec(
                if(flipX) rootMin.x else rootMax.x,
                if(flipY) rootMin.y else rootMax.y
        )

        val parent = component.relationships.parent
        if(parent != null && enabled) {
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