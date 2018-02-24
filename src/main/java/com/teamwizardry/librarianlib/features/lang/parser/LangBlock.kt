package com.teamwizardry.librarianlib.features.lang.parser

import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.lang.lexer.LangSymbol.*
import com.teamwizardry.librarianlib.features.lang.lexer.LangLexer
import com.teamwizardry.librarianlib.features.lang.lexer.LangToken

class LangBlock(val lexer: LangLexer, val location: LangToken?, val key: String, val parent: LangBlock?): LangMacroContext {
    val macros = mutableMapOf<String, LangMacro>()
    val entries = mutableListOf<LangMapping>()
    val blocks = mutableListOf<LangBlock>()

    val processed = mutableListOf<LangPair>()
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
                BLOCK_END -> break@infinite
                else -> token.unexpectedError(LANG_KEY, MACRO_DEFINE, BLOCK_END)
            }
        }
    }

    fun compute() {
        entries.map { entry ->
            val key = if(fullPrefix == "") entry.key else fullPrefix + "." + entry.key
            val value = entry.compute()

            processed.add(LangPair(entry.location, key, value))
        }

        blocks.forEach { it.compute() }
    }

    fun collect(): List<LangPair> {
        return processed + blocks.flatMap { it.collect() }
    }

    override fun findMacro(name: String): ILangMacro? {
        return macros[name] ?: parent?.findMacro(name)
    }

    private fun parseItemForLangKey(key: String, keyToken: LangToken) {
        val token = lexer.nextToken()
        when(token.symbol) {
            BLOCK_BEGIN -> {
                val block = LangBlock(lexer, keyToken, key, this)
                block.parse()
                blocks.add(block)
            }
            EXPRESSION_BEGIN -> {
                val mapping = LangMapping(lexer, keyToken, key, this)
                mapping.parse()
                entries.add(mapping)
            }
            else -> token.unexpectedError(BLOCK_BEGIN, EXPRESSION_BEGIN)
        }
    }

    private fun parseMacro() {
        val nameToken = lexer.nextToken()
        if (nameToken.symbol != IDENTIFIER) nameToken.unexpectedError(IDENTIFIER)
        val name = nameToken.value
        val macro = LangMacro(lexer, nameToken, name, this)

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

}
