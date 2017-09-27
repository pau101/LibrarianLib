package com.teamwizardry.librarianlib.test.testcore

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.utilities.client.GlCtx

/**
 * Created by TheCodeWarrior
 */
class LibTestClientProxy : LibTestCommonProxy() {
    @GlCtx
    fun test(customWorldRenderEvent: CustomWorldRenderEvent) {
        println("yes")
    }
}
