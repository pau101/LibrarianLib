package com.teamwizardry.librarianlib.features.base.block

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemDoor
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World



/**
 * The default implementation for an IVariantHolder item.
 */
@Suppress("LeakingThis")
open class ItemModDoor(block: BlockModDoor) : ItemModBlock(block) {
    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        var shiftedPos = pos
        if (facing != EnumFacing.UP)
            return EnumActionResult.FAIL

        var state = world.getBlockState(pos)
        val held = player.getHeldItem(hand)

        if (!state.block.isReplaceable(world, pos)) {
            shiftedPos = pos.offset(facing)
            state = world.getBlockState(shiftedPos)
        }

        if (player.canPlayerEdit(shiftedPos, facing, held) && block.canPlaceBlockAt(world, shiftedPos)) {
            val enumfacing = EnumFacing.fromAngle(player.rotationYaw.toDouble())
            val frontOffsetX = enumfacing.frontOffsetX
            val frontOffsetZ = enumfacing.frontOffsetZ
            val isRightHinge = frontOffsetX < 0 && hitZ < 0.5f || frontOffsetX > 0 && hitZ > 0.5f || frontOffsetZ < 0 && hitX > 0.5f || frontOffsetZ > 0 && hitX < 0.5f

            ItemDoor.placeDoor(world, shiftedPos, enumfacing, block, isRightHinge)
            val soundtype = state.block.getSoundType(state, world, shiftedPos, player)
            world.playSound(player, shiftedPos, soundtype.placeSound, SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f)
            held.shrink(1)
            return EnumActionResult.SUCCESS
        }

        return EnumActionResult.FAIL
    }
}

