package com.teamwizardry.librarianlib.features.exlang.parser

import com.teamwizardry.librarianlib.features.exlang.lexer.ExLangToken

class LexerException(message: String, token: ExLangToken? = null, cause: Throwable? = null):
        RuntimeException(token?.let { "$message Last token = $token" } ?: "$message at file start", cause)
class ParserException(message: String, token: ExLangToken? = null, cause: Exception? = null):
        RuntimeException(token?.let { "$message in ${token.locationString}" } ?: message, cause)
class EvaluationException(message: String, token: ExLangToken? = null, cause: Exception? = null):
        RuntimeException(token?.let { "$message in ${token.locationString}" } ?: message, cause)
