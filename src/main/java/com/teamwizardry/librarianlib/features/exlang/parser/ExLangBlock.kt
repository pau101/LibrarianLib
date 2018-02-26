package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangSymbol.*
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangLexer
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken

class ExLangBlock(val file: ExLangFile, val lexer: ExLangLexer, val location: ExLangToken?, val key: String,
                  val parent: ExLangBlock?): ExLangMacroContext {
    val macros = mutableMapOf<String, ExLangMacro>()
    val entries = mutableListOf<ExLangMapping>()
    val blocks = mutableListOf<ExLangBlock>()

    val processed = mutableListOf<ExLangPair>()
    val fullPrefix: String
        get() = (parent?.let { it.fullPrefix + "." } ?: "") + key

    fun parse() {
        infinite@ while(true) {
            val token = lexer.nextToken()
            when(token.symbol) {
                LANG_KEY -> {
                    val key = token.value
                    parseItemForLangKey(key, token)
                }
                MACRO_DEFINE -> {
                    parseMacro()
                }
                IMPORT_BEGIN -> {
                    parseImport(token)
                }
                BLOCK_BEGIN -> {
                    parseBlock(token)
                }
                BLOCK_END -> break@infinite
                else -> token.unexpectedError(LANG_KEY, MACRO_DEFINE, IMPORT_BEGIN, BLOCK_BEGIN, BLOCK_END)
            }
        }
    }

    fun compute() {
        entries.map { entry ->
            val key = if(fullPrefix == "") entry.key else fullPrefix + "." + entry.key
            val value = entry.compute()

            processed.add(ExLangPair(key, ExLangValue(value, entry.location)))
        }

        blocks.forEach { it.compute() }
    }

    fun collect(): List<ExLangPair> {
        return processed + blocks.flatMap { it.collect() }
    }

    override fun findMacro(name: String): IExLangMacro? {
        return macros[name] ?: parent?.findMacro(name)
    }

    private fun parseItemForLangKey(key: String, keyToken: ExLangToken) {
        val token = lexer.nextToken()
        when(token.symbol) {
            BLOCK_BEGIN -> {
                parseBlock(keyToken)
            }
            EXPRESSION_BEGIN -> {
                val mapping = ExLangMapping(lexer, keyToken, key, this)
                mapping.parse()
                entries.add(mapping)
            }
            else -> token.unexpectedError(BLOCK_BEGIN, EXPRESSION_BEGIN)
        }
    }

    private fun parseBlock(keyToken: ExLangToken) {
        val block = ExLangBlock(file, lexer, keyToken, "", this)
        block.parse()
        blocks.add(block)
    }

    private fun parseMacro() {
        val nameToken = lexer.nextToken()
        if (nameToken.symbol != IDENTIFIER) nameToken.unexpectedError(IDENTIFIER)
        val name = nameToken.value
        val macro = ExLangMacro(lexer, nameToken, name, this)

        val macroTypeToken = lexer.nextToken()
        when(macroTypeToken.symbol) {
            MACRO_DEFINITION_PARAMS_BEGIN -> {
                macro.parseParameters()
                val macroExpression = lexer.nextToken()
                if(macroExpression.symbol != EXPRESSION_BEGIN) macroExpression.unexpectedError(EXPRESSION_BEGIN)
                macro.parseExpression()
            }
            EXPRESSION_BEGIN -> macro.parseExpression()
            else -> macroTypeToken.unexpectedError(MACRO_DEFINITION_PARAMS_BEGIN, EXPRESSION_BEGIN)
        }
    }

    private fun parseImport(importToken: ExLangToken) {
        var path = ""
        infinite@while(true) {
            val token = lexer.nextToken()
            when(token.symbol) {
                PATH_COMPONENT -> {
                    path += token.value
                }
                PATH_END -> break@infinite
                else -> token.unexpectedError(PATH_COMPONENT, PATH_END)
            }
        }

        if (path.isEmpty()) throw ParserException("Path is missing", importToken)

        val file = file.createFile(path, importToken)
        file.root.macros.putAll(this.macros)
        file.parse()
        this.macros.clear()

        this.macros.putAll(file.root.macros)
        this.blocks.addAll(file.root.blocks)
        this.entries.addAll(file.root.entries)

        file.compute()
    }

}
