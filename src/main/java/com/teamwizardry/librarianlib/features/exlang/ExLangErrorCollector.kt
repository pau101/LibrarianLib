package com.teamwizardry.librarianlib.features.exlang

import org.antlr.v4.runtime.Token


class ExLangErrorCollector() {
    fun warn(reference: Token, message: String, vararg additionalReferences: Token) {}

    fun error(reference: Token, message: String, vararg additionalReferences: Token) {}

}
