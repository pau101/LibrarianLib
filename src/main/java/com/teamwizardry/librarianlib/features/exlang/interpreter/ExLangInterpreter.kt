package com.teamwizardry.librarianlib.features.exlang.interpreter

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.exlang.ExLangErrorCollector
import com.teamwizardry.librarianlib.features.exlang.ast.*
import org.antlr.v4.runtime.Token

class ExLangInterpreter(val errors: ExLangErrorCollector, context: ExLangASTContext) {
    private val rootContext = ExLangContext(errors, null, "", this)
    val entries = mutableMapOf<String, String>()
    val tokens = mutableMapOf<String, Token>()

    init {
        rootContext.processContext(context)
    }

    fun registerEntry(start: Token, end: Token, key: String, value: String) {
        if(key in entries) {
            errors.error(
                    reference = start,
                    message = "Entry $key is already mapped.",
                    additionalReferences = tokens[key]!!
            )
        } else {
            tokens[key] = start
        }
        entries[key] = value
    }
}

data class ExLangEntry(val token: Token, val key: String, val value: String)

class ExLangInterpreterException(message: String, cause: Throwable? = null): RuntimeException(message, cause)


private class ExLangContext(val errors: ExLangErrorCollector,
                            val parent: ExLangContext?, val prefix: String, val interpreter: ExLangInterpreter) {
    val macros = mutableMapOf<String, ExLangMacro>()

    val prefixWithTrailingDot: String
        get() = if(prefix == "") "" else prefix + "."

    fun processContext(context: ExLangASTContext) {
        context.statements.forEach { this.processStatement(it) }
    }

    fun processStatement(statement: ExLangASTStatement) {
        when(statement) {
            is ExLangASTEntry -> {
                interpreter.registerEntry(statement.start, statement.stop,
                        prefixWithTrailingDot + statement.key, evaluateExpression(statement.value))
            }
            is ExLangASTBlock -> {
                val subContext = ExLangContext(errors, this,
                        if(statement.key == "") prefix else prefixWithTrailingDot + statement.key, interpreter)
                subContext.processContext(statement.context)
            }
            is ExLangASTDefineMacroVariable -> {
                macros[statement.name] = ExLangMacro(errors, statement.start, statement.stop, statement.name,
                        emptyList(), evaluateExpression(statement.value, emptyList())
                )
            }
            is ExLangASTDefineMacroFunction -> {
                macros[statement.name] = ExLangMacro(errors, statement.start, statement.stop, statement.name,
                        statement.parameters, evaluateExpression(statement.value, statement.parameters)
                )
            }
            is ExLangASTUndefineMacro -> {
                macros.remove(statement.name)
            }
        }
    }

    fun findMacro(refToken: Token, name: String): ExLangMacro? {
        val macro = macros[name] ?: parent?.findMacro(refToken, name)
        if(parent == null && macro == null) {
            errors.error(
                    reference = refToken,
                    message = "Macro $name not found"
            )
        }
        return macro
    }

    fun evaluateExpression(expr: ExLangASTExpression): String {
        return evaluateExpression(expr, listOf()).parts.fold("") { acc, value ->
            acc + (value as ExLangASTValueString).text
        }
    }

    fun evaluateExpression(expr: ExLangASTExpression, parameters: List<String>): ExLangASTExpression {
        var hadNewlineEscape = false
        return ExLangASTExpression(expr.start, expr.stop, expr.parts.map { part ->
            val value = when(part) {
                is ExLangASTValueString -> {
                    var text = part.text
                    if(hadNewlineEscape) text = text.replace("\\A\\s+".toRegex(), "")
                    text = text.replace("$\\p{Blank}+".toRegex(), "")
                    ExLangASTValueString(part.start, part.stop, text)
                }
                is ExLangASTValueEscapeSequence -> {
                    if(part.character == "\n") hadNewlineEscape = true
                    ExLangASTValueString(part.start, part.stop, escapeSequences[part.character] ?: part.character)
                }
                is ExLangASTValueEscapeUnicode ->
                    ExLangASTValueString(part.start, part.stop, part.hex.toShort(16).toChar().toString())
                is ExLangASTValueMacroReference ->
                    if(part.name in parameters)
                        part
                    else
                        ExLangASTValueString(part.start, part.stop, findMacro(part.start, part.name)?.evaluate(part.start) ?: "")
                is ExLangASTValueMacroCall ->
                    ExLangASTValueString(part.start, part.stop, findMacro(part.start, part.name)
                            ?.evaluate(part.start, part.params.map { evaluateExpression(it) }) ?: "")
            }
            hadNewlineEscape = false
            if(part is ExLangASTValueEscapeSequence && part.character == "\n") {
                hadNewlineEscape = true
            }
            value
        })
    }

    val escapeSequences = mutableMapOf(
            "t" to "\t",
            "b" to "\b",
            "n" to "\n",
            "r" to "\r",
            "\n" to "",
            "\\" to "\\",
            "\"" to "\"",
            "'" to "'",
            "&" to "§"
    )
}

private class ExLangMacro(val errors: ExLangErrorCollector, val start: Token, val stop: Token, val name: String,
                          val parameterNames: List<String>, val expression: ExLangASTExpression) {
    fun evaluate(refToken: Token): String {
        if(parameterNames.isNotEmpty()) errors.error(
                reference = refToken,
                message = "Macro $name is a function.",
                additionalReferences = start
        )
        return expression.parts.fold("") { acc, value ->
            acc + (value as ExLangASTValueString).text
        }
    }

    fun evaluate(refToken: Token, params: List<String>): String {
        if(parameterNames.isEmpty()) errors.error(
                reference = refToken,
                message = "Macro $name is not a function.",
                additionalReferences = start
        )
        if(parameterNames.size != params.size) errors.error(
                reference = refToken,
                message = "Wrong number of arguments for macro $name. " +
                        "Expecting ${parameterNames.size}, got ${params.size}",
                additionalReferences = start
        )
        return expression.parts.fold("") { acc, value ->
            acc + when(value) {
                is ExLangASTValueString -> value.text
                is ExLangASTValueMacroReference -> {
                    if(value.name !in parameterNames)
                        throw ExLangInterpreterException("This should be impossible. Macro expression contained " +
                                "reference to `${value.name}` but it wasn't in the parameter list $parameterNames")
                    params[parameterNames.indexOf(value.name)]
                }
                else -> throw ExLangInterpreterException("Macro expression contained something besides string and " +
                        "macro reference values. All other types should be stripped before this point")
            }
        }
    }
}
