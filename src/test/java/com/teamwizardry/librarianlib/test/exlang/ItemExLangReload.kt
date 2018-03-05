package com.teamwizardry.librarianlib.test.exlang

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.exlang.ExLangLoader
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

class ItemExLangReload : ItemMod("exlang_reload") {

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {

        if (worldIn.isRemote) {
            try {
                ExLangLoader.reload(Minecraft.getMinecraft().resourceManager)
            } catch (t: Throwable) { // Gotta catch 'em all!
                t.printStackTrace()
            }
        }

        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand))
    }


}
