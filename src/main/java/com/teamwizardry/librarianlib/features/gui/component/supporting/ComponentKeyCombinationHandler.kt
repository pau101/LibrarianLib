package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard
import java.util.*

class ComponentKeyCombinationHandler(val component: GuiComponent) {

}

class KeyCombination(val key: Int, vararg val modifiers: ModifierKey) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyCombination

        if (key != other.key) return false
        if (!Arrays.equals(modifiers, other.modifiers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key
        result = 31 * result + Arrays.hashCode(modifiers)
        return result
    }
}

enum class ModifierKey(vararg val keycodes: Int) {
    CTRL(*if(Minecraft.IS_RUNNING_ON_MAC) intArrayOf(Keyboard.KEY_LMETA, Keyboard.KEY_RMETA) else intArrayOf(Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL)),
    ALT(Keyboard.KEY_LMENU, Keyboard.KEY_RMENU),
    SHIFT(Keyboard.KEY_LSHIFT, Keyboard.KEY_RSHIFT);

    fun isKeyDown() = keycodes.any { Keyboard.isKeyDown(it) }

    companion object {
        fun doModifiersMatch(vararg modifiers: ModifierKey): Boolean {
            return ModifierKey.values().all {
                it.isKeyDown() == modifiers.contains(it)
            }
        }
    }
}