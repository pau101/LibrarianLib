// Generated from /home/code/Documents/mods/LibrarianLib/src/main/java/com/teamwizardry/librarianlib/features/exlang/antlr/ExLangParser.g4 by ANTLR 4.7
package com.teamwizardry.librarianlib.features.exlang.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExLangParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExLangParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ExLangParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(ExLangParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(ExLangParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#entry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntry(ExLangParser.EntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(ExLangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirective(ExLangParser.DirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#importDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDirective(ExLangParser.ImportDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#undefineDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUndefineDirective(ExLangParser.UndefineDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#defineVarDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineVarDirective(ExLangParser.DefineVarDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#defineFuncDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineFuncDirective(ExLangParser.DefineFuncDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#defineParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineParams(ExLangParser.DefineParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExLangParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#stringExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringExpression(ExLangParser.StringExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#macroCallExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroCallExpression(ExLangParser.MacroCallExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExLangParser#macroReferenceExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroReferenceExpression(ExLangParser.MacroReferenceExpressionContext ctx);
}