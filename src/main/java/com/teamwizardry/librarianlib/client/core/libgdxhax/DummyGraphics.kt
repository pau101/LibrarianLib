package com.teamwizardry.librarianlib.client.core.libgdxhax

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglCursor
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.GLVersion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.SharedLibraryLoader
import org.lwjgl.LWJGLException
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Toolkit

/**
 * Created by TheCodeWarrior
 */
class DummyGraphics : Graphics {
    var gl30: GL30? = null
    internal val extensions = mutableSetOf<String>()

    private fun loadExtensions() {
        if (glVersion.isVersionEqualToOrHigher(3, 2)) {
            val numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS)
            for (i in 0..numExtensions - 1)
                extensions.add(org.lwjgl.opengl.GL30.glGetStringi(GL20.GL_EXTENSIONS, i))
        } else {
            extensions.addAll(org.lwjgl.opengl.GL11.glGetString(GL20.GL_EXTENSIONS).split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        }
    }
    override fun getPpiX(): Float {
        return Toolkit.getDefaultToolkit().screenResolution.toFloat()
    }

    override fun getPpiY(): Float {
        return Toolkit.getDefaultToolkit().screenResolution.toFloat()
    }

    override fun getPpcX(): Float {
        return Toolkit.getDefaultToolkit().screenResolution / 2.54f
    }

    override fun getPpcY(): Float {
        return Toolkit.getDefaultToolkit().screenResolution / 2.54f
    }

    override fun getDensity(): Float {
        return Toolkit.getDefaultToolkit().screenResolution / 160f
    }

    override fun supportsExtension(extension: String): Boolean {
        return extensions.contains(extension)
    }

    override fun isGL30Available(): Boolean {
        return gl30 != null
    }

    override fun getGL30(): GL30? {
        return gl30
    }

    // UNIMPLEMENTED ========================================================================

    override fun getDisplayModes(): kotlin.Array<out Graphics.DisplayMode> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDisplayModes(monitor: Graphics.Monitor?): kotlin.Array<out Graphics.DisplayMode> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCursor(cursor: Cursor?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFrameId(): Long {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVSync(vsync: Boolean) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGL20(): GL20 {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRawDeltaTime(): Float {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isFullscreen(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setResizable(resizable: Boolean) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitle(title: String?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setUndecorated(undecorated: Boolean) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFramesPerSecond(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMonitor(): Graphics.Monitor {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isContinuousRendering(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setWindowedMode(width: Int, height: Int): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setContinuousRendering(isContinuous: Boolean) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGLVersion(): GLVersion {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeight(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDeltaTime(): Float {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(): Graphics.GraphicsType {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDisplayMode(): Graphics.DisplayMode {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDisplayMode(monitor: Graphics.Monitor?): Graphics.DisplayMode {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSystemCursor(systemCursor: Cursor.SystemCursor?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBackBufferWidth(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPrimaryMonitor(): Graphics.Monitor {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setFullscreenMode(displayMode: Graphics.DisplayMode?): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBufferFormat(): Graphics.BufferFormat {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWidth(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestRendering() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMonitors(): kotlin.Array<out Graphics.Monitor> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newCursor(pixmap: Pixmap?, xHotspot: Int, yHotspot: Int): Cursor {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBackBufferHeight(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun supportsDisplayModeChange(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}