package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangLexer
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangSymbol.*
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken

class ExLangExpression(val lexer: ExLangLexer, val context: ExLangMacroContext) {
    private val parts = mutableListOf<IExpressionPart>()

    fun parse() {
        infinite@while(true) {
            val token = lexer.nextToken()
            val part = when(token.symbol) {
                STRING -> { PartString(token) }
                ESCAPED_CHARACTER -> { PartEscapedCharacter(token) }
                ESCAPED_CODEPOINT -> { PartEscapedCodepoint(token) }
                MACRO_REFERENCE -> { PartMacroReference(token, context) }
                EXPRESSION_END -> { break@infinite }
                else -> token.unexpectedError(STRING, ESCAPED_CHARACTER, ESCAPED_CODEPOINT, MACRO_REFERENCE,
                        EXPRESSION_END)
            }
            parts.add(part)
        }
    }

    fun compute(): String {
        if(parts.isEmpty()) return ""
        if(parts.size == 1) return parts.first().compute()

        return parts.joinToString("") { it.compute() }
    }
}

private interface IExpressionPart {
    fun compute(): String
}

private class PartString(val token: ExLangToken): IExpressionPart {
    override fun compute(): String {
        return token.value
    }
}

private class PartEscapedCharacter(val token: ExLangToken): IExpressionPart {
    override fun compute(): String {
        return escapeMap[token.value] ?:
                throw EvaluationException("Invalid escape sequence `\\${token.value}`. Valid sequences are " +
                        "(${escapeMap.map { (key, _) -> "\\$key" }})"
                )
    }

    companion object {
        val escapeMap = mutableMapOf(
                // normal escape substitutions //
                "t" to "\t",
                "b" to "\b",
                "n" to "\n",
                "r" to "\r",

                // escapes that map to themselves //
                "'" to "'",
                "\"" to "\"",
                "\\" to "\\",
                "$" to "$",
                " " to " ",

                // specials //
                "&" to "\u00A7" // section sign
        )
    }
}

private class PartEscapedCodepoint(val token: ExLangToken): IExpressionPart {
    override fun compute(): String {
        val short = token.value.toShort(16)
        return short.toChar().toString()
    }
}

private class PartMacroReference(val token: ExLangToken, val context: ExLangMacroContext): IExpressionPart {
    val parameters = mutableListOf<ExLangExpression>()
    var macro: IExLangMacro? = null

    fun parse(lexer: ExLangLexer) {
        macro = context.findMacro(token.value) ?: throw ParserException("Undefined macro ${token.value}", token)

        if (lexer.peekToken().symbol == MACRO_REFERENCE_PARAMS_BEGIN) {
            lexer.nextToken()
            infinite@while(true) {
                val expressionToken = lexer.nextToken()
                when(expressionToken.symbol) {
                    MACRO_REFERENCE_PARAMS_END -> break@infinite
                    EXPRESSION_BEGIN -> {
                        val expression = ExLangExpression(lexer, context)
                        expression.parse()
                        parameters.add(expression)
                    }
                    else -> expressionToken.unexpectedError(MACRO_REFERENCE_PARAMS_END, EXPRESSION_BEGIN)
                }
            }
        }
    }

    override fun compute(): String {
        val params = parameters.map { it.compute() }.toTypedArray()

        return macro?.computeValue(*params) ?:
                throw EvaluationException("Macro is null! This should never happen", token)
    }
}
