package com.teamwizardry.librarianlib.client.font

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.fx.shader.ShaderProgram
import com.teamwizardry.librarianlib.client.vbo.AttributeElement
import com.teamwizardry.librarianlib.client.vbo.EnumType
import com.teamwizardry.librarianlib.client.vbo.PositionElement
import com.teamwizardry.librarianlib.client.vbo.VertexFormat
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 * Created by TheCodeWarrior
 */
object FontRenderer {

    fun enableShader() {
        shader.use()
    }

    fun setShaderTexSize(tex: Texture) {
        shader.setUniformVar(FontRenderer.TEX_SIZE_UNIFORM, tex.width, tex.height)
    }

    fun disableShader() {
        shader.unbind()
    }

    val maxTextureSize by lazy {
        GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)
    }

    val shader: ShaderProgram by lazy {
        ShaderProgram(ResourceLocation(LibrarianLib.MODID, "text"))
    }

    val TEX_SIZE_UNIFORM by lazy {
        shader.getUniformLocation("texSize")
    }

    val format = TextFormat()
}

class TextFormat internal constructor(): VertexFormat(
        PositionElement(3),
        AttributeElement(FontRenderer.shader.getAttributeLocation("textColorIn"), EnumType.FLOAT, 4),
        AttributeElement(FontRenderer.shader.getAttributeLocation("shadowColorIn"), EnumType.FLOAT, 4),
        AttributeElement(FontRenderer.shader.getAttributeLocation("uvIn"), EnumType.FLOAT, 2)
) {
    private val pos: PositionElement = elements[0] as PositionElement
    private val color: AttributeElement = elements[1] as AttributeElement
    private val shadow: AttributeElement = elements[2] as AttributeElement
    private val uv: AttributeElement = elements[3] as AttributeElement

    fun pos(x: Number, y: Number, z: Number): TextFormat {
        pos.x = x.toFloat()
        pos.y = y.toFloat()
        pos.z = z.toFloat()
        return this
    }

    fun tex(u: Number, v: Number): TextFormat {
        uv.set(u, v)
        return this
    }

    fun color(r: Number, g: Number, b: Number, a: Number): TextFormat {
        color.set(r,g,b,a)
        return this
    }

    fun shadow(r: Number, g: Number, b: Number, a: Number): TextFormat {
        shadow.set(r,g,b,a)
        return this
    }
}