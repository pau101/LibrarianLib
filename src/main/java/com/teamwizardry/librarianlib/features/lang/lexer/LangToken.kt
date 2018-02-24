package com.teamwizardry.librarianlib.features.lang.lexer

import com.teamwizardry.librarianlib.features.lang.parser.ParserException

data class LangToken(val symbol: LangSymbol, val line: Int, val column: Int, val value: String) {
    fun unexpectedError(vararg expecting: LangSymbol): Nothing {
        val expectingStr =
                when {
                    expecting.isEmpty() -> ""
                    expecting.size == 1 -> " Expecting ${expecting[0]}"
                    expecting.size == 2 -> " Expecting ${expecting[0]} or ${expecting[1]}"
                    else -> " Expecting ${expecting.dropLast(1).joinToString(", ")}, or ${expecting.last()}"
                }
        throw ParserException("Unexpected token ${this.symbol}." + expectingStr, this)
    }
}
