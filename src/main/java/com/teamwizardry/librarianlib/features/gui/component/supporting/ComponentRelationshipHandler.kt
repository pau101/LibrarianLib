package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import java.util.*

/**
 * TODO: Document file ComponentRelationshipHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentRelationshipHandler(private val component: GuiComponent) {
    /** [GuiComponent.zIndex] */
    var zIndex = 0
    internal val components = mutableListOf<GuiComponent>()
    /** [GuiComponent.children] */
    val children: Collection<GuiComponent> = Collections.unmodifiableCollection(components)
        get() =
            if(component.opaque)
                emptyList()
            else
                field
    /**
     * An unmodifiable collection of all the children of this component, recursively.
     */
    val allChildren: Collection<GuiComponent>
        get() {
            if(component.opaque)
                return emptyList()
            else {
                val list = mutableListOf<GuiComponent>()
                addChildrenRecursively(list)
                return Collections.unmodifiableCollection(list)
            }
        }

    private fun addChildrenRecursively(list: MutableList<GuiComponent>) {
        list.addAll(components)
        components.forEach { it.relationships.addChildrenRecursively(list) }
    }

    internal val parents = mutableSetOf<GuiComponent>()

    /** [GuiComponent.parent] */
    var parent: GuiComponent? = null
        internal set(value) {
            parents.clear()
            if (value != null) {
                parents.addAll(value.relationships.parents)
                parents.add(value)
            }
            field = value
        }

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(vararg components: GuiComponent?) {
        if(component.opaque) throw IllegalStateException("Component is opaque")
        components.forEach { addInternal(it) }
    }

    private fun addInternal(component: GuiComponent?) {
        if (component == null) {
            LibrarianLog.error("Null component, ignoring")
            return
        }
        if (component === this.component)
            throw IllegalArgumentException("Immediately recursive component hierarchy")

        if (component.parent != null) {
            if (component.parent == this.component) {
                LibrarianLog.warn("You tried to add the component to the same parent twice. Why?")
                LibrarianLog.warnStackTrace()
                return
            } else {
                throw IllegalArgumentException("Component already had a parent")
            }
        }

        if (component in parents) {
            throw IllegalArgumentException("Recursive component hierarchy")
        }


        if (component.BUS.fire(GuiComponentEvents.AddChildEvent(this.component, component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.AddToParentEvent(component, this.component)).isCanceled())
            return
        components.add(component)
        component.relationships.parent = this.component

        component.BUS.fire(GuiComponentEvents.PostAddToParentEvent(component, this.component))
        component.layout.setNeedsLayout()
    }

    /**
     * @return whether this component has [component] as a decendant
     */
    operator fun contains(component: GuiComponent): Boolean =
            !component.opaque && (component in components || components.any { component in it.relationships })

    /**
     * Removes the supplied component
     * @param component
     */
    fun remove(component: GuiComponent) {
        if(component.opaque) throw IllegalStateException("Component is opaque")
        if (component !in components)
            return
        if (this.component.BUS.fire(GuiComponentEvents.RemoveChildEvent(this.component, component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(component, this.component)).isCanceled())
            return
        component.relationships.parent = null
        components.remove(component)
        component.layout.setNeedsLayout()
    }

    /**
     * Iterates over children while allowing children to be added or removed.
     */
    fun forEachChild(l: (GuiComponent) -> Unit) {
        if(component.opaque) throw IllegalStateException("Component is opaque")
        val copy = components.toList()
        copy.forEach(l)
    }

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    fun <C : GuiComponent> getByClass(clazz: Class<C>): List<C> {
        if(component.opaque) return listOf()
        val list = mutableListOf<C>()
        addByClass(clazz, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    fun <C : GuiComponent> getAllByClass(clazz: Class<C>): List<C> {
        if(component.opaque) return listOf()
        val list = mutableListOf<C>()
        addAllByClass(clazz, list)
        return list
    }

    protected fun <C : GuiComponent> addAllByClass(clazz: Class<C>, list: MutableList<C>) {
        addByClass(clazz, list)
        components.forEach { it.relationships.addAllByClass(clazz, list) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <C : GuiComponent> addByClass(clazz: Class<C>, list: MutableList<C>) {
        forEachChild { component ->
            if (clazz.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
    }

    /** [GuiComponent.root] */
    val root: GuiComponent
        get() {
            return parent?.root ?: this.component
        }

    /**
     * The descriptive path to this component through the component tree
     * Format:
     *
     * `Class:name[index].Class[index]...`
     *
     * - `.` separates parent from child
     * - `Class` is replaced with the class of the component
     * - `name` is the name of the component if it exists
     * - `[index]` is the number of duplicates of this component identifier that occur before this one
     */
    fun guiPath(endAt: GuiComponent? = null): String {
        val pathToParent = if(parent == endAt) "" else parent?.relationships?.guiPath()?.let { it + "/" } ?: ""

        var ourElement = ""

        ourElement += component.javaClass.simpleName // Just the classname, no package

        if(component.name != "")
            ourElement += ":" + component.name

        var occurances = 0
        var dups = 0
        parent?.relationships?.components?.forEach { child ->
            if(child == this.component) {
                dups = occurances
            }
            if(child.javaClass == this.javaClass && child.name == this.component.name)
                occurances++
        }
        if(occurances > 1)
            ourElement += "[$dups]"

        return pathToParent + ourElement
    }
}
