package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * An object that can be drawn to a bookcomponents
 * @author Pierce Corcoran
 */
interface IGuiDrawable {

    /**
     * Draw this object to the screen.
     * @param mousePos
     * *
     * @param partialTicks
     */
    @SideOnly(Side.CLIENT)
    fun draw(mousePos: Vec2d, partialTicks: Float)

}
