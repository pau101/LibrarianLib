package com.teamwizardry.librarianlib.features.gui.component

/**
 *
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Hook(val value: String = "", val priority: HookPriority = HookPriority.NORMAL)

enum class HookPriority {
    HIGHEST, //First to execute
    HIGH,
    NORMAL,
    LOW,
    LOWEST //Last to execute
}
