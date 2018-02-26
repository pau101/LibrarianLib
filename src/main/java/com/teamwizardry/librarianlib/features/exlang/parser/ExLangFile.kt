package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.kotlin.parentDirectory
import com.teamwizardry.librarianlib.features.exlang.ExLangLoader
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangLexer
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken
import net.minecraft.client.resources.IResourcePack
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader

class ExLangFile(val resourcePack: IResourcePack, val loc: ResourceLocation, val includeStack: List<ExLangToken>) {

    val root: ExLangBlock
    var prefix = loc.parentDirectory().resourcePath

    init {
        if(!resourcePack.resourceExists(loc)) {
            throw ParserException("Could not find file $loc in resource pack ${resourcePack.packName}")
        }
        val reader = ExLangLoader.checkHeader(loc, resourcePack.getInputStream(loc))
        val lexer = ExLangLexer(reader)
        lexer.file = loc

        root = ExLangBlock(this, lexer, null, "", null)
    }

    fun createFile(name: String, token: ExLangToken): ExLangFile {
        var path = FilenameUtils.normalize(name) ?: throw ParserException("Path `$name` cannot go below root!", token)

        if (!name.startsWith("/")) {
            path = FilenameUtils.normalize(name)
        }

        val loc = ResourceLocation(loc.resourceDomain, path)
        val stack = includeStack + listOf(token)
        if(includeStack.any { it.file == loc }) {
            val trace = stack.map { it.locationString }.joinToString { " => " }
            throw ParserException("Import recursion in pack ${resourcePack.packName}! $trace")
        }
        return ExLangFile(resourcePack, loc, stack)
    }

    fun parse() {
        root.parse()
    }

    fun compute() {
        root.compute()
    }

    fun collect(): LangEntryLists {
        val entries = root.collect()

        val grouped = entries.groupBy { it.key }.mapValues { it.value.map { it.value } }

        val duplicated = grouped.filter { (_, value) -> value.size > 1 }
        val pairs = grouped.filter { (_, value) -> value.size == 1 }.mapValues { it.value.first() } +
                duplicated.mapValues { it.value.last() }

        return LangEntryLists(pairs, duplicated)
    }
}

data class LangEntryLists(val entries: Map<String, ExLangValue>, val duplicated: Map<String, List<ExLangValue>>)
