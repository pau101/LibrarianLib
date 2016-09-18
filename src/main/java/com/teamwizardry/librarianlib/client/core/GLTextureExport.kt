package com.teamwizardry.librarianlib.client.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import net.minecraftforge.fml.common.FMLLog
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Created by TheCodeWarrior
 */
object GLTextureExport {

    fun saveGlTexture(name: String, mipmapLevels: Int) {

        if(!LibrarianLib.DEV_ENVIRONMENT)
            return;

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
        var level = 0

        while (true) {
            val width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH)
            val height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT)
            if(width == 0 || height == 0)
                break;
            val size = width * height

            val bufferedimage = BufferedImage(width, height, 2)
            val output = File(name + "_" + level + ".png")

            val buffer = BufferUtils.createIntBuffer(size)
            val data = IntArray(size)

            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer)
            buffer.get(data)
            bufferedimage.setRGB(0, 0, width, height, data, 0, width)

            try {
                ImageIO.write(bufferedimage, "png", output)
                LibrarianLog.info("[GLTextureExport] Exported png to: %s", output.getAbsolutePath())
            } catch (ioexception: IOException) {
                LibrarianLog.info("[GLTextureExport] Unable to write: ", ioexception)
            }
            level++
        }
    }

}