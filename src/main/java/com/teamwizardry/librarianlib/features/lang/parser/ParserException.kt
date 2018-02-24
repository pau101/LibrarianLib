package com.teamwizardry.librarianlib.features.lang.parser

import com.teamwizardry.librarianlib.features.lang.lexer.LangToken

class ParserException(message: String, token: LangToken? = null, cause: Exception? = null):
        RuntimeException(token?.let { "$message at line ${it.line}, column ${it.column}" } ?: message, cause)
class EvaluationException(message: String, token: LangToken? = null, cause: Exception? = null):
        RuntimeException(token?.let { "$message at line ${it.line}, column ${it.column}" } ?: message, cause)
