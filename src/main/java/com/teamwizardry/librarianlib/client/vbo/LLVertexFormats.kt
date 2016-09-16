package com.teamwizardry.librarianlib.client.vbo

import com.teamwizardry.librarianlib.client.font.BitmapFont
import com.teamwizardry.librarianlib.client.font.TextFormat
import com.teamwizardry.librarianlib.common.util.clamp
import net.minecraft.client.renderer.OpenGlHelper
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

/**
 * Created by TheCodeWarrior
 */

object PosColorFormat : VertexFormat(
        PositionElement(3),
        ColorElement()
) {
    private val pos: PositionElement = elements[0] as PositionElement
    private val color: ColorElement  = elements[1] as ColorElement

    fun pos(x: Number, y: Number, z: Number): PosColorFormat {
        pos.x = x.toFloat()
        pos.y = y.toFloat()
        pos.z = z.toFloat()
        return this
    }

    fun color(r: Number, g: Number, b: Number, a: Number): PosColorFormat {
        color.r = r.toFloat()
        color.g = g.toFloat()
        color.b = b.toFloat()
        color.a = a.toFloat()
        return this
    }
}

class PositionElement(count: Int) : VertexElement(EnumType.FLOAT, count.clamp(1..4)) {
    var x = 0f
    var y = 0f
    var z = 0f
    var w = 0f

    override fun push(vbo: VertexBuffer) {
        if(count >= 1) vbo.pushF(x)
        if(count >= 2) vbo.pushF(y)
        if(count >= 3) vbo.pushF(z)
        if(count == 4) vbo.pushF(w)
    }

    override fun setupRender(vbo: VertexBuffer, index: Int, stride: Int) {
        glVertexPointer(count, type.glConstant, stride, vbo.byteBuffer)
        glEnableClientState(GL_VERTEX_ARRAY)
    }

    override fun breakdownRender() {
        glDisableClientState(GL_VERTEX_ARRAY)
    }
}

class NormalElement : VertexElement(EnumType.FLOAT, 3) {
    var x = 0f
    var y = 0f
    var z = 0f

    override fun push(vbo: VertexBuffer) {
        vbo.pushF(x)
        vbo.pushF(y)
        vbo.pushF(z)
    }

    override fun setupRender(vbo: VertexBuffer, index: Int, stride: Int) {
        glNormalPointer(type.glConstant, stride, vbo.byteBuffer)
        glEnableClientState(GL_NORMAL_ARRAY)
    }

    override fun breakdownRender() {
        glDisableClientState(GL_NORMAL_ARRAY)
    }
}

class ColorElement : VertexElement(EnumType.FLOAT, 4) {
    var r = 0f
    var g = 0f
    var b = 0f
    var a = 0f

    override fun push(vbo: VertexBuffer) {
        vbo.pushF(r)
        vbo.pushF(g)
        vbo.pushF(b)
        vbo.pushF(a)
    }

    override fun setupRender(vbo: VertexBuffer, index: Int, stride: Int) {
        glColorPointer(count, type.glConstant, stride, vbo.byteBuffer)
        glEnableClientState(GL_COLOR_ARRAY)
    }

    override fun breakdownRender() {
        glDisableClientState(GL_COLOR_ARRAY)
    }
}

class UVElement(val texUnit: Int = 0) : VertexElement(EnumType.FLOAT, 2) {
    var u = 0f
    var v = 0f

    override fun push(vbo: VertexBuffer) {
        vbo.pushF(u)
        vbo.pushF(v)
    }

    override fun setupRender(vbo: VertexBuffer, index: Int, stride: Int) {
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + texUnit)
        glTexCoordPointer(count, type.glConstant, stride, vbo.byteBuffer)
        glEnableClientState(GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
    }

    override fun breakdownRender() {
        glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    }
}

class AttributeElement(val attr: Int, type: EnumType, count: Int) : VertexElement(type, count) {
    val arrF = FloatArray(count)
    val arrI = IntArray(count)
    val arrS = ShortArray(count)
    val arrB = ByteArray(count)

    override fun push(vbo: VertexBuffer) {
        for(i in 0..(count-1)) {
            if(type == EnumType.FLOAT)
                vbo.pushF(arrF[i])
            if(type == EnumType.INT || type == EnumType.UINT)
                vbo.pushI(arrI[i])
            if(type == EnumType.SHORT || type == EnumType.USHORT)
                vbo.pushS(arrS[i])
            if(type == EnumType.BYTE || type == EnumType.UBYTE)
                vbo.pushB(arrB[i])
        }
    }

    override fun setupRender(vbo: VertexBuffer, index: Int, stride: Int) {
        glEnableVertexAttribArray(attr);
        glVertexAttribPointer(attr, count, type.glConstant, false, stride, vbo.byteBuffer)
    }

    override fun breakdownRender() {
        glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    }

    fun set(vararg nums: Number) {
        if(type == EnumType.FLOAT)
            setF(*nums)
        if(type == EnumType.INT || type == EnumType.UINT)
            setI(*nums)
        if(type == EnumType.SHORT || type == EnumType.USHORT)
            setS(*nums)
        if(type == EnumType.BYTE || type == EnumType.UBYTE)
            setB(*nums)
    }

    fun setF(vararg nums: Number) {
        for(i in arrF.indices) {
            if(i < nums.size) {
                arrF[i] = nums[i].toFloat()
            } else {
                arrF[i] = 0f
            }
        }
    }

    fun setI(vararg nums: Number) {
        for(i in arrI.indices) {
            if(i < nums.size) {
                arrI[i] = nums[i].toInt()
            } else {
                arrI[i] = 0
            }
        }
    }

    fun setS(vararg nums: Number) {
        for(i in arrS.indices) {
            if(i < nums.size) {
                arrS[i] = nums[i].toShort()
            } else {
                arrS[i] = 0
            }
        }
    }

    fun setB(vararg nums: Number) {
        for(i in arrB.indices) {
            if(i < nums.size) {
                arrB[i] = nums[i].toByte()
            } else {
                arrB[i] = 0
            }
        }
    }
}