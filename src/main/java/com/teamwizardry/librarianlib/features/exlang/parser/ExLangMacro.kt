package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangLexer
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangSymbol.*
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken

class ExLangMacro(val lexer: ExLangLexer, val location: ExLangToken?, val name: String, val block: ExLangBlock?): IExLangMacro, ExLangMacroContext {
    val parameters = mutableMapOf<String, IExLangMacro>()
    val expression = ExLangExpression(lexer, this)

    override fun findMacro(name: String): IExLangMacro? {
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

class ConstantMacro(val token: ExLangToken): IExLangMacro {
    var value = ""
    override fun computeValue(vararg params: String): String {
        if(params.isNotEmpty()) throw EvaluationException("Parameters passed to non-function value", token)
        return value
    }
}

interface IExLangMacro {
    fun computeValue(vararg params: String): String
}

interface ExLangMacroContext {
    fun findMacro(name: String): IExLangMacro?
}
