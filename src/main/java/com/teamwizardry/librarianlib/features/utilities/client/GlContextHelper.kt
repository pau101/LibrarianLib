package com.teamwizardry.librarianlib.features.utilities.client

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.InvalidClassException
import java.util.*

/**
 * Created by Elad on 9/27/2017.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GlCtx

@SideOnly(Side.CLIENT)
object GlContextHelper {
    val functions = mutableListOf<(CustomWorldRenderEvent) -> Unit>()
    private val REQUIRED_PARAMETERS = arrayOf(CustomWorldRenderEvent::class.java)

    init {
        MinecraftForge.EVENT_BUS.register(this)
        AnnotationHelper.findAnnotatedMethods(annotationClass = GlCtx::class.java, callback = { method, array, info ->
            if (array != REQUIRED_PARAMETERS) throw InvalidClassException("$method annotated with GlCtx and has incorrect parameters of type ${Arrays.toString(array)}")
            else functions.add({ MethodHandleHelper.wrapperForMethod<Any?>(method)(null, arrayOf(it)) })
        })
    }

    @Mod.EventHandler
    fun worldRenderEvent(customWorldRenderEvent: CustomWorldRenderEvent) {
        functions.forEach { it(customWorldRenderEvent) }
    }
}
