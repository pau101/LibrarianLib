package com.teamwizardry.librarianlib.features.lang.parser

import com.teamwizardry.librarianlib.features.lang.lexer.LangLexer
import com.teamwizardry.librarianlib.features.lang.lexer.LangToken

class LangMapping(val lexer: LangLexer, val location: LangToken, var key: String, val block: LangBlock) {
    val expression = LangExpression(lexer, block)

    fun parse() {
        expression.parse()
    }

    fun compute(): String {
        return expression.compute()
    }
}

data class LangPair(val location: LangToken, val key: String, val value: String)
