package com.teamwizardry.librarianlib.client.vbo

import com.teamwizardry.librarianlib.common.util.clamp
import net.minecraft.client.renderer.OpenGlHelper
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

/**
 * Created by TheCodeWarrior
 */

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
}