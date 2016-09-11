package com.teamwizardry.librarianlib.client.core.libgdxhax

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
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
        
        this.duplicateBorder = delegate.duplicateBorder

        this.packToTexture = delegate.packToTexture
        this.pageWidth = delegate.pageWidth
        this.pageHeight = delegate.pageHeight
        this.pageFormat = delegate.pageFormat
        this.padding = delegate.padding
        this.duplicateBorder = delegate.duplicateBorder
        this.transparentColor = delegate.transparentColor
    }

    private var page_minFilter: Texture.TextureFilter? = null
    private var page_magFilter: Texture.TextureFilter? = null
    private var page_useMipMaps: Boolean = false
    private var page_queued = false

    private var reg_regions: Array<TextureRegion> = Array()
    private var reg_minFilter: Texture.TextureFilter? = null
    private var reg_magFilter: Texture.TextureFilter? = null
    private var reg_useMipMaps: Boolean = false
    private var reg_queued = false

    val queued: Boolean
        get() = page_queued || reg_queued

    override fun updatePageTextures(minFilter: Texture.TextureFilter?, magFilter: Texture.TextureFilter?, useMipMaps: Boolean) {
        try {
            delegate.updatePageTextures(minFilter, magFilter, useMipMaps)
        } catch(e: RuntimeException) {
            if(e.message?.startsWith("No OpenGL context") == true) {
                page_queued = true

                page_minFilter = minFilter
                page_magFilter = magFilter
                page_useMipMaps = useMipMaps
            } else {
                throw e
            }
        }
    }

    override fun updateTextureRegions(regions: Array<TextureRegion>?, minFilter: Texture.TextureFilter?, magFilter: Texture.TextureFilter?, useMipMaps: Boolean) {
        try {
            delegate.updateTextureRegions(regions, minFilter, magFilter, useMipMaps)
        } catch(e: RuntimeException) {
            if(e.message?.startsWith("No OpenGL context") == true) {
                reg_queued = true

                reg_minFilter = minFilter
                reg_magFilter = magFilter
                reg_useMipMaps = useMipMaps
                reg_regions.addAll(regions)
            } else {
                throw e
            }
        }
    }

    fun actualUpdatePageTextures() {
        this.updatePageTextures(page_minFilter, page_magFilter, page_useMipMaps)
        page_queued = false
        this.updateTextureRegions(reg_regions, reg_minFilter, reg_magFilter, reg_useMipMaps)
        reg_regions.clear()
        reg_queued = false
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

    override fun getTransparentColor(): Color {
        return delegate.getTransparentColor()
    }

    override fun getPageFormat(): Pixmap.Format {
        return delegate.getPageFormat()
    }

    override fun setPageFormat(pageFormat: Pixmap.Format?) {
        delegate.setPageFormat(pageFormat)
    }

    override fun getPages(): Array<Page> {
        return delegate.getPages()
    }

    override fun pack(image: Pixmap?): Rectangle {
        return delegate.pack(image)
    }

    override fun pack(name: String?, image: Pixmap?): Rectangle {
        return delegate.pack(name, image)
    }

    override fun setDuplicateBorder(duplicateBorder: Boolean) {
        delegate.setDuplicateBorder(duplicateBorder)
    }

    override fun getPageHeight(): Int {
        return delegate.getPageHeight()
    }

    override fun getPageIndex(name: String?): Int {
        return delegate.getPageIndex(name)
    }

    override fun getDuplicateBorder(): Boolean {
        return delegate.getDuplicateBorder()
    }

    override fun setTransparentColor(color: Color?) {
        delegate.setTransparentColor(color)
    }

    override fun setPageWidth(pageWidth: Int) {
        delegate.setPageWidth(pageWidth)
    }

    override fun getPackToTexture(): Boolean {
        return delegate.getPackToTexture()
    }

    override fun setPageHeight(pageHeight: Int) {
        delegate.setPageHeight(pageHeight)
    }

    override fun updateTextureAtlas(atlas: TextureAtlas?, minFilter: Texture.TextureFilter?, magFilter: Texture.TextureFilter?, useMipMaps: Boolean) {
        delegate.updateTextureAtlas(atlas, minFilter, magFilter, useMipMaps)
    }

    override fun sort(images: Array<Pixmap>?) {
        delegate.sort(images)
    }

    override fun generateTextureAtlas(minFilter: Texture.TextureFilter?, magFilter: Texture.TextureFilter?, useMipMaps: Boolean): TextureAtlas {
        return delegate.generateTextureAtlas(minFilter, magFilter, useMipMaps)
    }

    override fun getPage(name: String?): Page {
        return delegate.getPage(name)
    }

    override fun getPageWidth(): Int {
        return delegate.getPageWidth()
    }

    override fun getPadding(): Int {
        return delegate.getPadding()
    }

    override fun getRect(name: String?): Rectangle {
        return delegate.getRect(name)
    }

    override fun setPadding(padding: Int) {
        delegate.setPadding(padding)
    }

    override fun setPackToTexture(packToTexture: Boolean) {
        delegate.setPackToTexture(packToTexture)
    }

    override fun dispose() {
        delegate.dispose()
    }
}