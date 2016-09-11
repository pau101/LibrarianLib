@file:JvmName("ClientUtilMethods")

package com.teamwizardry.librarianlib.client.util

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.VertexBuffer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

// Color ===============================================================================================================

@SideOnly(Side.CLIENT)
fun Color.glColor() = GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)

// VertexBuffer

fun VertexBuffer.cache() : IntArray {
    this.finishDrawing()

    val intBuf = this.byteBuffer.asIntBuffer()
    val bufferInts = IntArray(intBuf.limit())
    for (i in bufferInts.indices) {
        bufferInts[i] = intBuf.get(i)
    }

    this.reset()
    return bufferInts
}

fun VertexBuffer.putCache(cache: IntArray) {
    this.addVertexData(cache)
}