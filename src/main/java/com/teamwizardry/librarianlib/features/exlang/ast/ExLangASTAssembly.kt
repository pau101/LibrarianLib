package com.teamwizardry.librarianlib.features.exlang.ast

import com.teamwizardry.librarianlib.features.exlang.antlr.ExLangParser;
import org.antlr.v4.runtime.tree.TerminalNode

fun ExLangParser.FileContext.toAst() : ExLangASTContext {
    val statements = this.element().mapNotNull { it.toAst() }
    return ExLangASTContext(this.start, this.stop, statements)
}

fun ExLangParser.ElementContext.toAst() : ExLangASTStatement? {
    return this.comment()?.toAst() ?:
            this.block()?.toAst() ?:
            this.directive()?.toAst() ?:
            this.entry()?.toAst()
}

fun ExLangParser.CommentContext.toAst() : ExLangASTStatement {
    return ExLangASTComment(this.start, this.stop, this.text)
}

fun ExLangParser.BlockContext.toAst() : ExLangASTBlock {
    val key = this.LANGUAGE_KEY()?.text ?: ""
    val statements = this.element().mapNotNull { it.toAst() }
    val context = ExLangASTContext(this.start, this.stop, statements)
    return ExLangASTBlock(this.start, this.stop, key, context)
}

fun ExLangParser.DirectiveContext.toAst() : ExLangASTStatement? {
    return this.undefineDirective()?.toAst() ?:
            this.defineFuncDirective()?.toAst() ?:
            this.defineVarDirective()?.toAst() ?:
            this.importDirective()?.toAst()
}

fun ExLangParser.EntryContext.toAst() : ExLangASTStatement {
    val key = this.LANGUAGE_KEY().text
    val value = this.expression().toAst()
    return ExLangASTEntry(this.start, this.stop, key, value)
}

fun ExLangParser.UndefineDirectiveContext.toAst() : ExLangASTStatement {
    val name = this.IDENTIFIER().text
    return ExLangASTUndefineMacro(this.start, this.stop, name)
}

fun ExLangParser.DefineVarDirectiveContext.toAst() : ExLangASTStatement {
    val name = this.IDENTIFIER().text
    val value = this.expression().toAst()
    return ExLangASTDefineMacroVariable(this.start, this.stop, name, value)
}

fun ExLangParser.DefineFuncDirectiveContext.toAst() : ExLangASTStatement {
    val name = this.IDENTIFIER().text
    val value = this.expression().toAst()
    val params = this.defineParams().toAst()
    return ExLangASTDefineMacroFunction(this.start, this.stop, name, params, value)
}

fun ExLangParser.DefineParamsContext.toAst() : List<String> {
    return this.IDENTIFIER().map { it.text }
}

fun ExLangParser.ImportDirectiveContext.toAst() : ExLangASTStatement {
    var text = this.STRING().text
    text = text.substring(1, text.length-1)
    return ExLangASTImport(this.start, this.stop, text)
}

fun ExLangParser.ExpressionContext.toAst() : ExLangASTExpression {
    return this.macroReferenceExpression()?.toAst()?.let { ExLangASTExpression(this.start, this.stop, listOf(it)) } ?:
            this.macroCallExpression()?.toAst()?.let { ExLangASTExpression(this.start, this.stop, listOf(it)) } ?:
            this.stringExpression()?.toAst() ?:
            ExLangASTExpression(this.start, this.stop, listOf(ExLangASTValueString(this.start, this.stop, "")))
}

fun ExLangParser.MacroReferenceExpressionContext.toAst() : ExLangASTValue {
    return ExLangASTValueMacroReference(this.start, this.stop,
            this.MACRO_REF().text.removePrefix("$").removePrefix("{").removeSuffix("}")
    )
}

fun ExLangParser.MacroCallExpressionContext.toAst() : ExLangASTValue {
    return ExLangASTValueMacroCall(this.start, this.stop,
            this.MACRO_CALL().text.removePrefix("$").removeSuffix("("),
            this.expression().map { it.toAst() }
    )
}

fun ExLangParser.StringExpressionContext.toAst() : ExLangASTExpression {
    return ExLangASTExpression(this.start, this.stop, this.children.mapNotNull {
        when (it) {
            is TerminalNode -> {
                when (it.symbol.type) {
                    ExLangParser.ESCAPE_SEQUENCE -> ExLangASTValueEscapeSequence(it.symbol, it.symbol, it.text.removePrefix("\\"))
                    ExLangParser.ESCAPE_UNICODE -> ExLangASTValueEscapeUnicode(it.symbol, it.symbol, it.text.removePrefix("\\u"))
                    ExLangParser.TEXT -> ExLangASTValueString(it.symbol, it.symbol, it.text)
                    else -> null
                }
            }
            is ExLangParser.MacroCallExpressionContext -> it.toAst()
            is ExLangParser.MacroReferenceExpressionContext -> it.toAst()
            else -> null
        }
    })
}
