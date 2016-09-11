package com.teamwizardry.librarianlib.client.core.libgdxhax

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GLContext
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class MyPixmapPacker(val delegate: PixmapPacker, val packStrategy: PackStrategy) : PixmapPacker(delegate.pageWidth, delegate.pageHeight, delegate.pageFormat, delegate.padding, delegate.duplicateBorder, packStrategy) {
    init {
        instances.add(this)
    }

    private var minFilter: Texture.TextureFilter? = null
    private var magFilter: Texture.TextureFilter? = null
    private var useMipMaps: Boolean = false
    private var queued = false

    override fun updatePageTextures(minFilter: Texture.TextureFilter?, magFilter: Texture.TextureFilter?, useMipMaps: Boolean) {
        try {
            super.updatePageTextures(minFilter, magFilter, useMipMaps)
        } catch(e: RuntimeException) {
            if(e.message?.startsWith("No OpenGL context") == true) {
                queued = true

                this.minFilter = minFilter
                this.magFilter = magFilter
                this.useMipMaps = useMipMaps
            } else {
                throw e
            }
        }
    }

    fun actualUpdatePageTextures() {
        super.updatePageTextures(minFilter, magFilter, useMipMaps)
    }

    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        val instances: MutableSet<MyPixmapPacker> = Collections.newSetFromMap(WeakHashMap<MyPixmapPacker, Boolean>())

        @SubscribeEvent
        fun updateTextures(event: TickEvent.RenderTickEvent) {
            if(event.phase == TickEvent.Phase.START) {
                for(packer in instances) {
                    if(packer.queued)
                        packer.actualUpdatePageTextures()
                }
            }
        }
    }
}