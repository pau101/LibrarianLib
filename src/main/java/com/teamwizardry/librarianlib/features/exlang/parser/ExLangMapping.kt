package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangLexer
import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken

class ExLangMapping(val lexer: ExLangLexer, val location: ExLangToken, var key: String, val block: ExLangBlock) {
    val expression = ExLangExpression(lexer, block)

    fun parse() {
        expression.parse()
    }

    fun compute(): String {
        return expression.compute()
    }
}

data class ExLangPair(val key: String, val value: ExLangValue)
data class ExLangValue(val text: String, val location: ExLangToken)
