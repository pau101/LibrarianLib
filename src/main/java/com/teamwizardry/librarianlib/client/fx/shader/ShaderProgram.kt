package com.teamwizardry.librarianlib.client.fx.shader

import net.minecraft.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.LWJGLException
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.FloatBuffer

/**
 * Created by TheCodeWarrior
 */
class ShaderProgram {
    protected var buf16Pool: FloatBuffer? = null

    /**
     * Makes the "default shader" (0) the active program. In GL 3.1+ core profile,
     * you may run into glErrors if you try rendering with the default shader.
     */
    fun unbind() {
        GL20.glUseProgram(0)
    }

    var program: Int
    var vertex: Int
    var fragment: Int
    protected var log: String? = null

    /**
     * Gets shaders from `ResourceLocation + '.vert'` and `ResourceLocation + '.frag'`
     */
    @Throws(LWJGLException::class)
    constructor(location: ResourceLocation): this(
            readFileAsString("/assets/${location.resourceDomain}/shader/${location.resourcePath}.vert"),
            readFileAsString("/assets/${location.resourceDomain}/shader/${location.resourcePath}.frag"),
            null)

    @Throws(LWJGLException::class)
    constructor(vertexSource: String, fragmentSource: String): this(vertexSource, fragmentSource, null)

    /**
     * Creates a new shader from vertex and fragment source, and with the given
     * map of , String> attrib locations
     * @param vertexShader the vertex shader source string
     * *
     * @param fragmentShader the fragment shader source string
     * *
     * @param attributes a map of attrib locations for GLSL 120
     * *
     * @throws LWJGLException if the program could not be compiled and linked
     */
    @Throws(LWJGLException::class)
    constructor(vertexShader: String, fragmentShader: String, attributes: Map<Int, String>?) {
        //compile the String source
        vertex = compileShader(vertexShader, GL20.GL_VERTEX_SHADER)
        fragment = compileShader(fragmentShader, GL20.GL_FRAGMENT_SHADER)

        //create the program
        program = GL20.glCreateProgram()

        //attach the shaders
        GL20.glAttachShader(program, vertex)
        GL20.glAttachShader(program, fragment)

        //bind the attrib locations for GLSL 120
        if (attributes != null)
            for ((k, v) in attributes.entries)
                GL20.glBindAttribLocation(program, k, v)

        //link our program
        GL20.glLinkProgram(program)

        //grab our info log
        val infoLog = GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH))

        //if some log exists, append it
        if (infoLog != null && infoLog!!.trim({ it <= ' ' }).length != 0)
            log += infoLog

        //if the link failed, throw some sort of exception
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) === GL11.GL_FALSE)
            throw LWJGLException(
                    "Failure in linking program. Error log:\n" + infoLog!!)

        //detach and delete the shaders which are no longer needed
        GL20.glDetachShader(program, vertex)
        GL20.glDetachShader(program, fragment)
        GL20.glDeleteShader(vertex)
        GL20.glDeleteShader(fragment)
    }

    /** Compile the shader source as the given type and return the shader object ID.  */
    @Throws(LWJGLException::class)
    protected fun compileShader(source: String, type: Int): Int {
        //create a shader object
        val shader = GL20.glCreateShader(type)
        //pass the source string
        GL20.glShaderSource(shader, source)
        //compile the source
        GL20.glCompileShader(shader)

        //if info/warnings are found, append it to our shader log
        val infoLog = GL20.glGetShaderInfoLog(shader,
                GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH))
        if (infoLog != null && infoLog!!.trim({ it <= ' ' }).length != 0)
            log += getName(type) + ": " + infoLog + "\n"

        //if the compiling was unsuccessful, throw an exception
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) === GL11.GL_FALSE)
            throw LWJGLException("Failure in compiling " + getName(type)
                    + ". Error log:\n" + infoLog)

        return shader
    }

    protected fun getName(shaderType: Int): String {
        if (shaderType == GL20.GL_VERTEX_SHADER)
            return "GL_VERTEX_SHADER"
        if (shaderType == GL20.GL_FRAGMENT_SHADER)
            return "GL_FRAGMENT_SHADER"
        else
            return "shader"
    }

    /**
     * Make this shader the active program.
     */
    fun use() {
        GL20.glUseProgram(program)
    }

    /**
     * Destroy this shader program.
     */
    fun destroy() {
        GL20.glDeleteProgram(program)
    }

    /**
     * Gets the location of the specified uniform name.
     * @param str the name of the uniform
     * *
     * @return the location of the uniform in this program
     */
    fun getUniformLocation(str: String): Int {
        return GL20.glGetUniformLocation(program, str)
    }

    /* ------ UNIFORM SETTERS/GETTERS ------ */

    /**
     * Sets the uniform data at the specified location (the uniform type may be int, bool or sampler2D).
     * @param loc the location of the int/bool/sampler2D uniform
     * *
     * @param i the value to set
     */
    fun setUniformi(loc: Int, i: Int) {
        if (loc == -1) return
        GL20.glUniform1i(loc, i)
    }

    /**
     * Sends a 4x4 matrix to the shader program.
     * @param loc the location of the mat4 uniform
     * *
     * @param transposed whether the matrix should be transposed
     * *
     * @param mat the matrix to send
     */
    fun setUniformMatrix(loc: Int, transposed: Boolean, mat: Matrix4f) {
        if (loc == -1) return
        if (buf16Pool == null)
            buf16Pool = BufferUtils.createFloatBuffer(16)
        buf16Pool!!.clear()
        mat.store(buf16Pool)
        buf16Pool!!.flip()
        GL20.glUniformMatrix4(loc, transposed, buf16Pool)
    }

    companion object {
        private fun readFileAsString(filename: String): String {
            val source = StringBuilder()
            val stream = ShaderProgram::class.java.getResourceAsStream(filename)
            var exception: Exception? = null
            val reader: BufferedReader

            if (stream == null)
                return ""

            try {
                reader = BufferedReader(InputStreamReader(stream, "UTF-8"))

                var innerExc: Exception? = null
                try {
                    var line: String? = reader.readLine()
                    while (line != null) {
                        source.append(line).append('\n')
                        line = reader.readLine()
                    }
                } catch (exc: Exception) {
                    exception = exc
                }

                try {
                    reader.close()
                } catch (exc: Exception) {
                    if (innerExc == null)
                        innerExc = exc
                    else
                        exc.printStackTrace()
                }

                if (innerExc != null)
                    throw innerExc
            } catch (exc: Exception) {
                exception = exc
            } finally {
                try {
                    stream.close()
                } catch (exc: Exception) {
                    if (exception == null)
                        exception = exc
                    else
                        exc.printStackTrace()
                }

                if (exception != null)
                    throw exception
            }

            return source.toString()
        }
    }
}