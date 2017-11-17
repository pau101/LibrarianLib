package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d

class ComponentLayoutShortcuts(private val component: GuiComponent) {

    /** The anchor corresponding to the minimum x coordinate of the component's bounds */
    @JvmField val left = component.layout.left
    /** The anchor corresponding to the maximum x coordinate of the component's bounds */
    @JvmField val right = component.layout.right
    /** The anchor corresponding to the minimum y coordinate of the component's bounds */
    @JvmField val top = component.layout.top
    /** The anchor corresponding to the maximum y coordinate of the component's bounds */
    @JvmField val bottom = component.layout.bottom
    /** The anchor corresponding to the center x coordinate of the component's bounds */
    @JvmField val centerX = component.layout.centerX
    /** The anchor corresponding to the center y coordinate of the component's bounds */
    @JvmField val centerY = component.layout.centerY

    /** The anchor corresponding to the width of the component's bounds */
    @JvmField val width = component.layout.width
    /** The anchor corresponding to the height of the component's bounds */
    @JvmField val height = component.layout.height

    @JvmOverloads
    fun boundsEqualTo(other: GuiComponent, topLeftPlus: Vec2d = Vec2d.ZERO, bottomRightPlus: Vec2d = Vec2d.ZERO) {
        left.equalTo(other.l.left + topLeftPlus.x)
        top.equalTo(other.l.top + topLeftPlus.y)
        right.equalTo(other.l.right + bottomRightPlus.x)
        bottom.equalTo(other.l.bottom + bottomRightPlus.y)
    }

    @JvmOverloads
    fun posEqualTo(other: GuiComponent, plus: Vec2d = Vec2d.ZERO) {
        left.equalTo(other.l.left + plus.x)
        top.equalTo(other.l.top + plus.y)
    }

    @JvmOverloads
    fun sizeEqualTo(other: GuiComponent, plus: Vec2d = Vec2d.ZERO) {
        width.equalTo(other.l.width + plus.x)
        height.equalTo(other.l.height + plus.y)
    }
}