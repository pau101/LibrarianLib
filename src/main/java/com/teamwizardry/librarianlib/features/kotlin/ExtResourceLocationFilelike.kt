package com.teamwizardry.librarianlib.features.kotlin

import net.minecraft.util.ResourceLocation
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.nio.file.Paths

fun ResourceLocation.parentDirectory()
        = ResourceLocation(this.resourceDomain, FilenameUtils.getPath(this.resourcePath).removeSuffix("/"))

val ResourceLocation.fileName: String
    get() = FilenameUtils.getName(this.resourcePath)

val ResourceLocation.extension: String
    get() = FilenameUtils.getExtension(this.resourcePath)

fun ResourceLocation.resolve(path: String) = ResourceLocation(this.resourceDomain, FilenameUtils.concat(this.resourcePath, path))
