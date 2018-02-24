package com.teamwizardry.librarianlib.features.lang.parser

import com.teamwizardry.librarianlib.features.lang.lexer.LangLexer
import com.teamwizardry.librarianlib.features.lang.lexer.LangSymbol.*
import com.teamwizardry.librarianlib.features.lang.lexer.LangToken

class LangMacro(val lexer: LangLexer, val location: LangToken?, val name: String, val block: LangBlock?): ILangMacro, LangMacroContext {
    val parameters = mutableMapOf<String, ILangMacro>()
    val expression = LangExpression(lexer, this)

    override fun findMacro(name: String): ILangMacro? {
        return parameters[name] ?: block?.findMacro(name)
    }

    override fun computeValue(vararg params: String): String {
        if(params.size > parameters.size)
            throw EvaluationException("Too many parameters passed to macro. ${params.size} passed, should be ${parameters.size}", location)
        if(params.size < parameters.size)
            throw EvaluationException("Too few parameters passed to macro. ${params.size} passed, should be ${parameters.size}", location)
        return expression.compute()
    }

    fun parseParameters() {
        infinite@while(true) {
            val token = lexer.nextToken()
            when(token.symbol) {
                MACRO_DEFINITION_PARAMS_END -> break@infinite
                IDENTIFIER -> {
                    if(token.value in parameters) throw ParserException("Duplicate parameter name", token)
                    parameters[token.value] = ConstantMacro(token)
                }
                else -> token.unexpectedError(IDENTIFIER, MACRO_DEFINITION_PARAMS_END)
            }
        }
    }

    fun parseExpression() {
        expression.parse()
    }
}

class ConstantMacro(val token: LangToken): ILangMacro {
    var value = ""
    override fun computeValue(vararg params: String): String {
        if(params.isNotEmpty()) throw EvaluationException("Parameters passed to non-function value", token)
        return value
    }
}

interface ILangMacro {
    fun computeValue(vararg params: String): String
}

interface LangMacroContext {
    fun findMacro(name: String): ILangMacro?
}
