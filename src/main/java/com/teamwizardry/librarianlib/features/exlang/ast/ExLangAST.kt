package com.teamwizardry.librarianlib.features.exlang.ast

import com.teamwizardry.librarianlib.features.kotlin.indent
import org.antlr.v4.runtime.Token

data class ExLangASTContext(val start: Token, val stop: Token, var statements: List<ExLangASTStatement>) {
    override fun toString() = "ExLangContext(statments=[\n" + statements.joinToString("\n").indent() + "\n])"
}

data class ExLangASTExpression(val start: Token, val stop: Token, val parts: List<ExLangASTValue>) {
    override fun toString() = "ExLangExpression(parts=[\n" + parts.joinToString("\n").indent() + "\n])"
}

sealed class ExLangASTStatement
data class ExLangASTComment(val start: Token, val stop: Token, val text: String) : ExLangASTStatement()
data class ExLangASTEntry(val start: Token, val stop: Token, val key: String, val value: ExLangASTExpression) : ExLangASTStatement()
data class ExLangASTBlock(val start: Token, val stop: Token, val key: String, val context: ExLangASTContext) : ExLangASTStatement()
data class ExLangASTDefineMacroVariable(val start: Token, val stop: Token, val name: String, val value: ExLangASTExpression) : ExLangASTStatement()
data class ExLangASTUndefineMacro(val start: Token, val stop: Token, val name: String) : ExLangASTStatement()
data class ExLangASTDefineMacroFunction(val start: Token, val stop: Token, val name: String, val parameters: List<String>, val value: ExLangASTExpression) : ExLangASTStatement()
data class ExLangASTImport(val start: Token, val stop: Token, val value: String) : ExLangASTStatement()

sealed class ExLangASTValue
data class ExLangASTValueString(val start: Token, val stop: Token, val text: String) : ExLangASTValue()
data class ExLangASTValueEscapeSequence(val start: Token, val stop: Token, val character: String) : ExLangASTValue()
data class ExLangASTValueEscapeUnicode(val start: Token, val stop: Token, val hex: String) : ExLangASTValue()
data class ExLangASTValueMacroReference(val start: Token, val stop: Token, val name: String) : ExLangASTValue()
data class ExLangASTValueMacroCall(val start: Token, val stop: Token, val name: String, val params: List<ExLangASTExpression>) : ExLangASTValue()

class ExLangParserException(message: String, cause: Throwable? = null): RuntimeException(message, cause)
