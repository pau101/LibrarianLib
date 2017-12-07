package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import org.apache.commons.lang3.exception.ExceptionUtils

class ComponentDebugHandler(val component: GuiComponent) {
    val creationStackTrace: List<String>

    init {
        val componentClassName = component.javaClass.canonicalName
        val trace = ExceptionUtils.getStackTrace(Throwable()).lines()
        creationStackTrace = trace.subList(trace.indexOfFirst { componentClassName in it }, trace.size)

        component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) {
            if(component.geometry.mouseOverDirectly && ModifierKey.doModifiersMatch(ModifierKey.CTRL, ModifierKey.ALT)) {
                LibrarianLog.debug("Clicked component with CTRL and ALT pressed:")
                LibrarianLog.debug("| > GUI Path: `${component.relationships.guiPath()}`")
                LibrarianLog.debug("| > Creation stacktrace:")
                creationStackTrace.forEach {
                    LibrarianLog.debug("| $it")
                }
            }
        }
    }
}