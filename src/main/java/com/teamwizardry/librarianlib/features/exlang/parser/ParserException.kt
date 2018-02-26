package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken

class ParserException(message: String, token: ExLangToken? = null, cause: Exception? = null):
        RuntimeException(token?.let { "$message in ${token.locationString}" } ?: message, cause)
class EvaluationException(message: String, token: ExLangToken? = null, cause: Exception? = null):
        RuntimeException(token?.let { "$message in ${token.locationString}" } ?: message, cause)
