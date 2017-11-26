package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.supporting.ComponentLayoutHandler
import com.teamwizardry.librarianlib.features.helpers.vec

data class Margins(val top: Int, val right: Int, val bottom: Int, val left: Int)

fun ComponentLayoutHandler.boundsEqualTo(other: GuiComponent, margins: Margins)
        = this.boundsEqualTo(other, vec(margins.left, margins.top), vec(-margins.right, -margins.bottom))
