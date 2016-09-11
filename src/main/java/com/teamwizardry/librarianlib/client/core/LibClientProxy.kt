package com.teamwizardry.librarianlib.client.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.client.core.libgdxhax.DummyApplication
import com.teamwizardry.librarianlib.client.core.libgdxhax.DummyGraphics
import com.teamwizardry.librarianlib.client.font.FontLoader
import com.teamwizardry.librarianlib.client.fx.shader.LibShaders
import com.teamwizardry.librarianlib.client.fx.shader.ShaderHelper
import com.teamwizardry.librarianlib.client.newbook.BookCommand
import com.teamwizardry.librarianlib.client.sprite.SpritesMetadataSection
import com.teamwizardry.librarianlib.client.sprite.SpritesMetadataSectionSerializer
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.client.util.ScissorUtil
import com.teamwizardry.librarianlib.common.core.LibCommonProxy
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import org.lwjgl.opengl.GLContext
import java.lang.ref.WeakReference
import java.util.*

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
class LibClientProxy : LibCommonProxy(), IResourceManagerReloadListener {

    override var bookInstance: Book? = null
        private set

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)

        initLibGDX()

        bookInstance = Book(LibrarianLib.MODID)

        if (LibrarianLib.DEV_ENVIRONMENT)
            ClientCommandHandler.instance.registerCommand(BookCommand())

        ScissorUtil
        LibShaders
        ShaderHelper.initShaders()

        ModelHandler.preInit()

        val s = ReflectionHelper.findField(Minecraft::class.java, "metadataSerializer_", "field_110452_an").get(Minecraft.getMinecraft()) as MetadataSerializer //todo methodhandle
        s.registerMetadataSectionType(SpritesMetadataSectionSerializer(), SpritesMetadataSection::class.java)
        SpritesMetadataSection.registered = true

        if (Minecraft.getMinecraft().resourceManager is IReloadableResourceManager)
            (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener(this)

        onResourceManagerReload(Minecraft.getMinecraft().resourceManager)

        FontLoader
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)
        ModelHandler.init()
    }

    override fun translate(s: String, vararg format: Any?): String {
        return I18n.format(s, *format)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        val newList = ArrayList<WeakReference<Texture>>()

        for (tex in Texture.textures) {
            tex.get()?.loadSpriteData()
            if (tex.get() != null) newList.add(tex)
        }

        Texture.textures = newList
    }

    fun initLibGDX() {
        val contextcapabilities = GLContext.getCapabilities()

        LwjglNativesLoader.load()

        var gl20: GL20? = null
        var gl30: GL30? = null
        if (contextcapabilities.OpenGL30) {
            val constructor = Class.forName("com.badlogic.gdx.backends.lwjgl.LwjglGL30").getDeclaredConstructor()
            constructor.isAccessible = true
            gl30 = constructor.newInstance() as GL30?
            gl20 = gl30
        } else {
            val constructor = Class.forName("com.badlogic.gdx.backends.lwjgl.LwjglGL20").getDeclaredConstructor()
            constructor.isAccessible = true
            gl20 = constructor.newInstance() as GL20?
        }

        Gdx.app = DummyApplication()
        val g = DummyGraphics()
        g.gl30 = gl30
        Gdx.graphics = g
        Gdx.gl = gl20
        Gdx.gl20 = gl20
        Gdx.gl30 = gl30
    }
}
