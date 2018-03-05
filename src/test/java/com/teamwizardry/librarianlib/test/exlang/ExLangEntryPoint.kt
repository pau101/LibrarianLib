package com.teamwizardry.librarianlib.test.exlang

import com.google.common.eventbus.Subscribe
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import com.teamwizardry.librarianlib.test.testcore.TestLog
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Created by TheCodeWarrior
 */
object ExLangEntryPoint: TestEntryPoint {

    override fun preInit(event: FMLPreInitializationEvent) {
        ExLangItemRegister
    }

    override fun init(event: FMLInitializationEvent) {
        ClientRunnable {
            (Minecraft.getMinecraft().resourceManager as? IReloadableResourceManager)?.registerReloadListener(ExLangTester)
        }.runIfClient()
    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }
}

object ExLangItemRegister {
    val reload = ItemExLangReload()
}


object ExLangTester: IResourceManagerReloadListener  {
    init { MinecraftForge.EVENT_BUS.register(this) }

    var doTest = false

    override fun onResourceManagerReload(resourceManager: IResourceManager?) {
        doTest = true
    }

    @SubscribeEvent
    fun event(e: GuiScreenEvent.DrawScreenEvent) {
        if(doTest) {
            doTest = false

            tests.forEach {
                it.checks.clear()
                it.run()
            }
            tests.forEach {
                it.printSuccesses()
            }
            tests.forEach {
                it.printFailures()
            }

        }
    }

    val tests = listOf(
            PlainKeysTest
//            BlockTest,
//            MacroVariablesTest,
//            MacroFunctionsTest,
//            KeylessBlockTest,
//            IncludeTest
    )

}

abstract class ExLangTest {
    val checks = mutableListOf<TestCheck>()

    abstract fun run()

    fun test(message: String, callback: TestCheck.() -> Unit): TestCheck {
        val check = TestCheck(message)
        checks.add(check)
        try {
            check.callback()
        } catch(e: Throwable) {
            check.exception = e
        }
        return check
    }

    inner class TestCheck(val message: String) {
        var exception: Throwable? = null
        val failures = mutableListOf<TestFailure>()

        operator fun String.rem(other: String): Boolean {
            val translated = I18n.format(this)
            if(translated != other) {
                failures.add(TestFailure(this, other, translated))
                return false
            }
            return true
        }
    }

    fun printSuccesses() {
        checks.forEach {
            if (it.failures.isEmpty() && it.exception == null) {
                TestLog.info("${it.message} succeeded")
            }
        }
    }

    fun printFailures() {
        checks.forEach {
            if (it.failures.isNotEmpty() || it.exception != null) {
                TestLog.error("${it.message} failed")
                val width = it.failures.maxBy { it.key.length }?.key?.length ?: 8
                if(it.exception != null) TestLog.error("", it.exception)
                it.failures.forEach {
                    TestLog.error("%${width}s".format(it.key) + " | expecting `${it.expectedValue}`")
                    TestLog.error(" " * width + " | got `${it.translatedValue}`")
                }
            }
        }
    }
}
data class TestFailure(val key: String, val expectedValue: String, val translatedValue: String)

