package com.teamwizardry.librarianlib.features.exlang.ast

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.exlang.ExLangLoader
import com.teamwizardry.librarianlib.features.exlang.antlr.ExLangLexer
import com.teamwizardry.librarianlib.features.exlang.antlr.ExLangParser
import com.teamwizardry.librarianlib.features.kotlin.resolve
import net.minecraft.client.resources.IResourcePack
import net.minecraft.util.ResourceLocation
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.apache.commons.io.FilenameUtils
import java.util.*

class ExLangPack(val resourcePack: IResourcePack, val root: ResourceLocation) {
    private val parsing = LinkedList<ResourceLocation>()
    private val cache = mutableMapOf<ResourceLocation, ExLangASTContext>()

    operator fun get(name: String) : ExLangASTContext {
        val isBelowRoot = FilenameUtils.normalize(name) == null
        val file = root.resolve(name)
        if(!resourcePack.resourceExists(file)) {
            throw RuntimeException("Missing file $file")
        }
        if(parsing.contains(file)) {
            throw RuntimeException("Recursive imports: $parsing")
        }
        parsing.push(file)
        val reader = ExLangLoader.checkHeader(file, resourcePack.getInputStream(file))
        val lexer = ExLangLexer(CharStreams.fromReader(reader, "${resourcePack.packName}@$file"))
        val tokens = CommonTokenStream(lexer)
        val parser = ExLangParser(tokens)
        val context = parser.file().toAst()

        processImports(context, if(isBelowRoot) "" else FilenameUtils.getPath(name))

        cache[file] = context
        return context
    }

    fun processImports(context: ExLangASTContext, workingDir: String) {
        context.statements = context.statements.flatMap {
            if(it is ExLangASTImport) {
                val resolved = FilenameUtils.concat(workingDir, it.value + ".exlang") ?:
                        throw ExLangParserException("Import path `${it.value}` from working directory " +
                                "`$workingDir` drops below root")

                return@flatMap this[resolved].statements
            } else {
                if(it is ExLangASTBlock) {
                    processImports(it.context, workingDir)
                }
                return@flatMap listOf(it)
            }
        }
    }
}
