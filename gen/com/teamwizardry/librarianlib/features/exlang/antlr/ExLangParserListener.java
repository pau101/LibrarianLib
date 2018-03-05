// Generated from /home/code/Documents/mods/LibrarianLib/src/main/java/com/teamwizardry/librarianlib/features/exlang/antlr/ExLangParser.g4 by ANTLR 4.7
package com.teamwizardry.librarianlib.features.exlang.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExLangParser}.
 */
public interface ExLangParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExLangParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ExLangParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ExLangParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(ExLangParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(ExLangParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(ExLangParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(ExLangParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#entry}.
	 * @param ctx the parse tree
	 */
	void enterEntry(ExLangParser.EntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#entry}.
	 * @param ctx the parse tree
	 */
	void exitEntry(ExLangParser.EntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(ExLangParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(ExLangParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirective(ExLangParser.DirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirective(ExLangParser.DirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#importDirective}.
	 * @param ctx the parse tree
	 */
	void enterImportDirective(ExLangParser.ImportDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#importDirective}.
	 * @param ctx the parse tree
	 */
	void exitImportDirective(ExLangParser.ImportDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#undefineDirective}.
	 * @param ctx the parse tree
	 */
	void enterUndefineDirective(ExLangParser.UndefineDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#undefineDirective}.
	 * @param ctx the parse tree
	 */
	void exitUndefineDirective(ExLangParser.UndefineDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#defineVarDirective}.
	 * @param ctx the parse tree
	 */
	void enterDefineVarDirective(ExLangParser.DefineVarDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#defineVarDirective}.
	 * @param ctx the parse tree
	 */
	void exitDefineVarDirective(ExLangParser.DefineVarDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#defineFuncDirective}.
	 * @param ctx the parse tree
	 */
	void enterDefineFuncDirective(ExLangParser.DefineFuncDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#defineFuncDirective}.
	 * @param ctx the parse tree
	 */
	void exitDefineFuncDirective(ExLangParser.DefineFuncDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#defineParams}.
	 * @param ctx the parse tree
	 */
	void enterDefineParams(ExLangParser.DefineParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#defineParams}.
	 * @param ctx the parse tree
	 */
	void exitDefineParams(ExLangParser.DefineParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExLangParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExLangParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#stringExpression}.
	 * @param ctx the parse tree
	 */
	void enterStringExpression(ExLangParser.StringExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#stringExpression}.
	 * @param ctx the parse tree
	 */
	void exitStringExpression(ExLangParser.StringExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#macroCallExpression}.
	 * @param ctx the parse tree
	 */
	void enterMacroCallExpression(ExLangParser.MacroCallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#macroCallExpression}.
	 * @param ctx the parse tree
	 */
	void exitMacroCallExpression(ExLangParser.MacroCallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExLangParser#macroReferenceExpression}.
	 * @param ctx the parse tree
	 */
	void enterMacroReferenceExpression(ExLangParser.MacroReferenceExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExLangParser#macroReferenceExpression}.
	 * @param ctx the parse tree
	 */
	void exitMacroReferenceExpression(ExLangParser.MacroReferenceExpressionContext ctx);
}