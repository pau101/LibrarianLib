package com.teamwizardry.librarianlib.test.cap

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.features.kotlin.toComponent
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge

/**
 * Created by Elad on 1/22/2017.
 */
class CapabilityTestV2(override val clazz: Class<TestCap> = TestCap::class.java) : CapabilityMod<TestCap>() {

    @Save
    val testCap = TestCap(0)
    companion object {
        init {
            println("Init")
            MinecraftForge.EVENT_BUS.register(CapabilityTestV2::class.java)
            BlockCapTest2()
        }
    }

}

class BlockCapTest2 : BlockMod("hi", Material.ROCK), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        playerIn?.sendStatusMessage((worldIn?.getTileEntity(pos) as? TileEntityCapTest2)!!.cap.testCap.a++.toString().toComponent(), false)
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
        return TileEntityCapTest2()
    }

}

@TileRegister("hi")
class TileEntityCapTest2 : TileMod() {
    @CapabilityProvide(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN)
    val cap: CapabilityTestV2 = CapabilityTestV2()
}

class TestCap(var a: Int)