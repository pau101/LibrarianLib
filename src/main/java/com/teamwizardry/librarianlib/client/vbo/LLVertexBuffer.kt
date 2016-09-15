package com.teamwizardry.librarianlib.client.vbo

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.util.cache
import io.netty.util.internal.ConcurrentSet
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

/**
 * Created by TheCodeWarrior
 */
class VertexBuffer(bufferSizeIn: Int) {

    companion object {
        val INSTANCE = VertexBuffer(65536)
    }

    private var vertexFormat: VertexFormat? = null
    var byteBuffer: ByteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn)

    internal fun link(vertexFormat: VertexFormat) {
        this.vertexFormat = vertexFormat
    }

    fun pushB(v: Byte) {
        growBufferToFit(1)
        byteBuffer.put(v)
    }

    fun pushI(v: Int) {
        growBufferToFit(4)
        byteBuffer.putInt(v)
    }

    fun pushS(v: Short) {
        growBufferToFit(2)
        byteBuffer.putShort(v)
    }

    fun pushF(v: Float) {
        growBufferToFit(4)
        byteBuffer.putFloat(v)
    }

    fun finishDrawing(limit: Int) {
        byteBuffer.position(0)
        byteBuffer.limit(limit)
    }

    fun reset() {
        byteBuffer.position(0)
        byteBuffer.limit(byteBuffer.capacity())
    }

    fun growBufferToFit(toAdd: Int) {
        if(toAdd > byteBuffer.capacity()-byteBuffer.position()) {
            val oldSize = byteBuffer.capacity()
            val newSize = oldSize + MathHelper.roundUp(toAdd, 1 shl 16)
            val pos = byteBuffer.position()

            LibrarianLog.info("Had to expand vertex buffer ByteBuffer from $oldSize to $newSize in order to fit $toAdd new bytes")

            val newBuffer = GLAllocation.createDirectByteBuffer(newSize)
            byteBuffer.position(0)

            newBuffer.put(byteBuffer)
            newBuffer.position(pos)
            this.byteBuffer = newBuffer
        }
    }
}

abstract class VertexFormat(vararg val elements: VertexElement) {
    val stride = elements.sumBy { it.size() }

    protected var linked: VertexBuffer? = null
    var vertexCount = 0
        protected set

    fun start(buffer: VertexBuffer) {
        linked = buffer
        buffer.link(this)
    }

    fun endVertex() {
        buf { buf ->
            for(elem in elements) {
                elem.push(buf)
            }
            vertexCount++
        }
    }

    /**
     * Do something if the vertex buffer is linked
     */
    protected fun <T> buf(lambda: (VertexBuffer) -> T): T? {
        val l = linked
        if(l != null)
            return lambda(l)
        return null
    }

    fun draw(drawMode: Int) {
        buf { vbo ->
            vbo.finishDrawing(stride*vertexCount)

            var i = 0
            for (elem in elements) {
                vbo.byteBuffer.position(i)
                elem.setupRender(vbo, i, stride)
                i += elem.size()
            }
            vbo.byteBuffer.position(0)

            GlStateManager.glDrawArrays(drawMode, 0, vertexCount)

            for (elem in elements) {
                elem.breakdownRender()
            }

            vbo.reset()
            linked = null
            vertexCount = 0
        }
    }

    fun cache(): VboCache? {
        return buf { vbo ->
            vbo.finishDrawing(stride*vertexCount)

            val byteBuf = vbo.byteBuffer
            val bufferBytes = ByteArray(byteBuf.limit())
            for (i in bufferBytes.indices) {
                bufferBytes[i] = byteBuf.get(i)
            }

            val c = VboCache(this, vertexCount, bufferBytes)

            vbo.reset()
            linked = null
            vertexCount = 0

            return@buf c
        }
    }

    fun addCache(cache: VboCache) {
        buf { vbo ->
            if(cache.format != this)
                throw IllegalArgumentException("Format ${this.javaClass} is not compatible with cache's format ${cache.format.javaClass}")
            vbo.growBufferToFit(cache.buffer.size)
            vbo.byteBuffer.put(cache.buffer)
            vertexCount += cache.vertexCount
        }
    }
}

data class VboCache(val format: VertexFormat, val vertexCount: Int, val buffer: ByteArray)

abstract class VertexElement protected constructor(val type: EnumType, val count: Int) {

    /**
     * Push the values on to the vbo passed
     */
    abstract fun push(vbo: VertexBuffer)

    /**
     * Sets up the rendering. Returns the index to be passed in to the next element
     */
    abstract fun setupRender(vbo: VertexBuffer, index: Int, stride: Int)

    /**
     * Breaks down the rendering after the vertices have been rendered
     */
    abstract fun breakdownRender()

    fun size(): Int = type.size * count

}

enum class EnumType(val size: Int, val displayName: String, val glConstant: Int) {
    FLOAT(4, "Float", 5126),
    UBYTE(1, "Unsigned Byte", 5121),
    BYTE(1, "Byte", 5120),
    USHORT(2, "Unsigned Short", 5123),
    SHORT(2, "Short", 5122),
    UINT(4, "Unsigned Int", 5125),
    INT(4, "Int", 5124);
}