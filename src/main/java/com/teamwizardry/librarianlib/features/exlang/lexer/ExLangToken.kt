package com.teamwizardry.librarianlib.features.exlang.lexer

import com.teamwizardry.librarianlib.features.exlang.parser.ParserException
import net.minecraft.util.ResourceLocation

data class ExLangToken(val symbol: ExLangSymbol, val file: ResourceLocation, val line: Int, val column: Int, val value: String) {
    fun unexpectedError(vararg expecting: ExLangSymbol): Nothing {
        val expectingStr =
                when {
                    expecting.isEmpty() -> ""
                    expecting.size == 1 -> " Expecting ${expecting[0]}"
                    expecting.size == 2 -> " Expecting ${expecting[0]} or ${expecting[1]}"
                    else -> " Expecting ${expecting.dropLast(1).joinToString(", ")}, or ${expecting.last()}"
                }
        throw ParserException("Unexpected token ${this.symbol}." + expectingStr, this)
    }

    val locationString: String
        get() {
            return "$file(l$line:c$column)"
        }
}
