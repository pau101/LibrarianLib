package com.teamwizardry.librarianlib.features.gui.pastry

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.util.ResourceLocation

class PastryStyle(val location: ResourceLocation) {

    val texture = Texture(location)

    companion object {
        @JvmStatic val VANILLA = PastryStyle("librarianlib:textures/libgui/styles/vanilla.png".toRl())

        var currentStyle: PastryStyle = VANILLA
            private set

        fun useStyle(style: PastryStyle) {
            currentStyle = style
        }

        fun getSprite(name: String, w: Int, h: Int): Sprite {
            return currentStyle.texture.getSprite(name, w, h)
        }
    }
}