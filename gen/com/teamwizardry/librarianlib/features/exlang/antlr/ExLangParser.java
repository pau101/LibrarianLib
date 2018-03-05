// Generated from /home/code/Documents/mods/LibrarianLib/src/main/java/com/teamwizardry/librarianlib/features/exlang/antlr/ExLangParser.g4 by ANTLR 4.7
package com.teamwizardry.librarianlib.features.exlang.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, BLOCK_BEGIN=2, BLOCK_END=3, EQUALS=4, DEFINE=5, UNDEFINE=6, 
		IMPORT=7, STRING=8, LANGUAGE_KEY=9, ROOT_WHITESPACE=10, IDENTIFIER_ONLY_WHITESPACE=11, 
		EXPRESSION_WHITESPACE=12, STRING_BEGIN=13, MACRO_REF=14, MACRO_CALL=15, 
		PARAM_SEPARATOR=16, PARAMS_END=17, MACRO_ARGS_WHITESPACE=18, ESCAPE_SEQUENCE=19, 
		ESCAPE_UNICODE=20, STRING_END=21, TEXT=22, PARAMS_BEGIN=23, IDENTIFIER=24, 
		DEFINE_WHITESPACE=25, BARE_MACRO_PREFIX=26, S_STRING_END=27;
	public static final int
		RULE_file = 0, RULE_element = 1, RULE_comment = 2, RULE_entry = 3, RULE_block = 4, 
		RULE_directive = 5, RULE_importDirective = 6, RULE_undefineDirective = 7, 
		RULE_defineVarDirective = 8, RULE_defineFuncDirective = 9, RULE_defineParams = 10, 
		RULE_expression = 11, RULE_stringExpression = 12, RULE_macroCallExpression = 13, 
		RULE_macroReferenceExpression = 14;
	public static final String[] ruleNames = {
		"file", "element", "comment", "entry", "block", "directive", "importDirective", 
		"undefineDirective", "defineVarDirective", "defineFuncDirective", "defineParams", 
		"expression", "stringExpression", "macroCallExpression", "macroReferenceExpression"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'{'", "'}'", "'='", "'#define'", "'#undefine'", "'#import'", 
		null, null, null, null, null, null, null, null, null, "')'", null, null, 
		null, null, null, "'('", null, null, null, "'''"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "COMMENT", "BLOCK_BEGIN", "BLOCK_END", "EQUALS", "DEFINE", "UNDEFINE", 
		"IMPORT", "STRING", "LANGUAGE_KEY", "ROOT_WHITESPACE", "IDENTIFIER_ONLY_WHITESPACE", 
		"EXPRESSION_WHITESPACE", "STRING_BEGIN", "MACRO_REF", "MACRO_CALL", "PARAM_SEPARATOR", 
		"PARAMS_END", "MACRO_ARGS_WHITESPACE", "ESCAPE_SEQUENCE", "ESCAPE_UNICODE", 
		"STRING_END", "TEXT", "PARAMS_BEGIN", "IDENTIFIER", "DEFINE_WHITESPACE", 
		"BARE_MACRO_PREFIX", "S_STRING_END"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ExLangParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ExLangParser.EOF, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COMMENT) | (1L << BLOCK_BEGIN) | (1L << DEFINE) | (1L << UNDEFINE) | (1L << IMPORT) | (1L << LANGUAGE_KEY))) != 0)) {
				{
				{
				setState(30);
				element();
				}
				}
				setState(35);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(36);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElementContext extends ParserRuleContext {
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public EntryContext entry() {
			return getRuleContext(EntryContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(38);
				comment();
				}
				break;
			case 2:
				{
				setState(39);
				directive();
				}
				break;
			case 3:
				{
				setState(40);
				entry();
				}
				break;
			case 4:
				{
				setState(41);
				block();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(ExLangParser.COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntryContext extends ParserRuleContext {
		public TerminalNode LANGUAGE_KEY() { return getToken(ExLangParser.LANGUAGE_KEY, 0); }
		public TerminalNode EQUALS() { return getToken(ExLangParser.EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public EntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitEntry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntryContext entry() throws RecognitionException {
		EntryContext _localctx = new EntryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_entry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(LANGUAGE_KEY);
			setState(47);
			match(EQUALS);
			setState(48);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode BLOCK_BEGIN() { return getToken(ExLangParser.BLOCK_BEGIN, 0); }
		public TerminalNode BLOCK_END() { return getToken(ExLangParser.BLOCK_END, 0); }
		public TerminalNode LANGUAGE_KEY() { return getToken(ExLangParser.LANGUAGE_KEY, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LANGUAGE_KEY) {
				{
				setState(50);
				match(LANGUAGE_KEY);
				}
			}

			setState(53);
			match(BLOCK_BEGIN);
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COMMENT) | (1L << BLOCK_BEGIN) | (1L << DEFINE) | (1L << UNDEFINE) | (1L << IMPORT) | (1L << LANGUAGE_KEY))) != 0)) {
				{
				{
				setState(54);
				element();
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60);
			match(BLOCK_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DirectiveContext extends ParserRuleContext {
		public UndefineDirectiveContext undefineDirective() {
			return getRuleContext(UndefineDirectiveContext.class,0);
		}
		public DefineVarDirectiveContext defineVarDirective() {
			return getRuleContext(DefineVarDirectiveContext.class,0);
		}
		public DefineFuncDirectiveContext defineFuncDirective() {
			return getRuleContext(DefineFuncDirectiveContext.class,0);
		}
		public ImportDirectiveContext importDirective() {
			return getRuleContext(ImportDirectiveContext.class,0);
		}
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_directive);
		try {
			setState(66);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(62);
				undefineDirective();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(63);
				defineVarDirective();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(64);
				defineFuncDirective();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(65);
				importDirective();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportDirectiveContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(ExLangParser.IMPORT, 0); }
		public TerminalNode STRING() { return getToken(ExLangParser.STRING, 0); }
		public ImportDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterImportDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitImportDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitImportDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportDirectiveContext importDirective() throws RecognitionException {
		ImportDirectiveContext _localctx = new ImportDirectiveContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_importDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(IMPORT);
			setState(69);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UndefineDirectiveContext extends ParserRuleContext {
		public TerminalNode UNDEFINE() { return getToken(ExLangParser.UNDEFINE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(ExLangParser.IDENTIFIER, 0); }
		public UndefineDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_undefineDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterUndefineDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitUndefineDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitUndefineDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UndefineDirectiveContext undefineDirective() throws RecognitionException {
		UndefineDirectiveContext _localctx = new UndefineDirectiveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_undefineDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(UNDEFINE);
			setState(72);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefineVarDirectiveContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(ExLangParser.DEFINE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(ExLangParser.IDENTIFIER, 0); }
		public TerminalNode EQUALS() { return getToken(ExLangParser.EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DefineVarDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineVarDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterDefineVarDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitDefineVarDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitDefineVarDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefineVarDirectiveContext defineVarDirective() throws RecognitionException {
		DefineVarDirectiveContext _localctx = new DefineVarDirectiveContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_defineVarDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(DEFINE);
			setState(75);
			match(IDENTIFIER);
			setState(76);
			match(EQUALS);
			setState(77);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefineFuncDirectiveContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(ExLangParser.DEFINE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(ExLangParser.IDENTIFIER, 0); }
		public TerminalNode PARAMS_BEGIN() { return getToken(ExLangParser.PARAMS_BEGIN, 0); }
		public DefineParamsContext defineParams() {
			return getRuleContext(DefineParamsContext.class,0);
		}
		public TerminalNode PARAMS_END() { return getToken(ExLangParser.PARAMS_END, 0); }
		public TerminalNode EQUALS() { return getToken(ExLangParser.EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DefineFuncDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineFuncDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterDefineFuncDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitDefineFuncDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitDefineFuncDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefineFuncDirectiveContext defineFuncDirective() throws RecognitionException {
		DefineFuncDirectiveContext _localctx = new DefineFuncDirectiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_defineFuncDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(DEFINE);
			setState(80);
			match(IDENTIFIER);
			setState(81);
			match(PARAMS_BEGIN);
			setState(82);
			defineParams();
			setState(83);
			match(PARAMS_END);
			setState(84);
			match(EQUALS);
			setState(85);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefineParamsContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(ExLangParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(ExLangParser.IDENTIFIER, i);
		}
		public List<TerminalNode> PARAM_SEPARATOR() { return getTokens(ExLangParser.PARAM_SEPARATOR); }
		public TerminalNode PARAM_SEPARATOR(int i) {
			return getToken(ExLangParser.PARAM_SEPARATOR, i);
		}
		public DefineParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineParams; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterDefineParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitDefineParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitDefineParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefineParamsContext defineParams() throws RecognitionException {
		DefineParamsContext _localctx = new DefineParamsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_defineParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(IDENTIFIER);
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAM_SEPARATOR) {
				{
				{
				setState(88);
				match(PARAM_SEPARATOR);
				setState(89);
				match(IDENTIFIER);
				}
				}
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public StringExpressionContext stringExpression() {
			return getRuleContext(StringExpressionContext.class,0);
		}
		public MacroCallExpressionContext macroCallExpression() {
			return getRuleContext(MacroCallExpressionContext.class,0);
		}
		public MacroReferenceExpressionContext macroReferenceExpression() {
			return getRuleContext(MacroReferenceExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_BEGIN:
				{
				setState(95);
				stringExpression();
				}
				break;
			case MACRO_CALL:
				{
				setState(96);
				macroCallExpression();
				}
				break;
			case MACRO_REF:
				{
				setState(97);
				macroReferenceExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringExpressionContext extends ParserRuleContext {
		public TerminalNode STRING_BEGIN() { return getToken(ExLangParser.STRING_BEGIN, 0); }
		public TerminalNode STRING_END() { return getToken(ExLangParser.STRING_END, 0); }
		public List<TerminalNode> ESCAPE_SEQUENCE() { return getTokens(ExLangParser.ESCAPE_SEQUENCE); }
		public TerminalNode ESCAPE_SEQUENCE(int i) {
			return getToken(ExLangParser.ESCAPE_SEQUENCE, i);
		}
		public List<TerminalNode> ESCAPE_UNICODE() { return getTokens(ExLangParser.ESCAPE_UNICODE); }
		public TerminalNode ESCAPE_UNICODE(int i) {
			return getToken(ExLangParser.ESCAPE_UNICODE, i);
		}
		public List<TerminalNode> TEXT() { return getTokens(ExLangParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(ExLangParser.TEXT, i);
		}
		public List<MacroCallExpressionContext> macroCallExpression() {
			return getRuleContexts(MacroCallExpressionContext.class);
		}
		public MacroCallExpressionContext macroCallExpression(int i) {
			return getRuleContext(MacroCallExpressionContext.class,i);
		}
		public List<MacroReferenceExpressionContext> macroReferenceExpression() {
			return getRuleContexts(MacroReferenceExpressionContext.class);
		}
		public MacroReferenceExpressionContext macroReferenceExpression(int i) {
			return getRuleContext(MacroReferenceExpressionContext.class,i);
		}
		public StringExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterStringExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitStringExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitStringExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringExpressionContext stringExpression() throws RecognitionException {
		StringExpressionContext _localctx = new StringExpressionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_stringExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			match(STRING_BEGIN);
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MACRO_REF) | (1L << MACRO_CALL) | (1L << ESCAPE_SEQUENCE) | (1L << ESCAPE_UNICODE) | (1L << TEXT))) != 0)) {
				{
				setState(106);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ESCAPE_SEQUENCE:
					{
					setState(101);
					match(ESCAPE_SEQUENCE);
					}
					break;
				case ESCAPE_UNICODE:
					{
					setState(102);
					match(ESCAPE_UNICODE);
					}
					break;
				case TEXT:
					{
					setState(103);
					match(TEXT);
					}
					break;
				case MACRO_CALL:
					{
					setState(104);
					macroCallExpression();
					}
					break;
				case MACRO_REF:
					{
					setState(105);
					macroReferenceExpression();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(111);
			match(STRING_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MacroCallExpressionContext extends ParserRuleContext {
		public TerminalNode MACRO_CALL() { return getToken(ExLangParser.MACRO_CALL, 0); }
		public TerminalNode PARAMS_END() { return getToken(ExLangParser.PARAMS_END, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> PARAM_SEPARATOR() { return getTokens(ExLangParser.PARAM_SEPARATOR); }
		public TerminalNode PARAM_SEPARATOR(int i) {
			return getToken(ExLangParser.PARAM_SEPARATOR, i);
		}
		public MacroCallExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macroCallExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterMacroCallExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitMacroCallExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitMacroCallExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MacroCallExpressionContext macroCallExpression() throws RecognitionException {
		MacroCallExpressionContext _localctx = new MacroCallExpressionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_macroCallExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			match(MACRO_CALL);
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING_BEGIN) | (1L << MACRO_REF) | (1L << MACRO_CALL))) != 0)) {
				{
				setState(114);
				expression();
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==PARAM_SEPARATOR) {
					{
					{
					setState(115);
					match(PARAM_SEPARATOR);
					setState(116);
					expression();
					}
					}
					setState(121);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(124);
			match(PARAMS_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MacroReferenceExpressionContext extends ParserRuleContext {
		public TerminalNode MACRO_REF() { return getToken(ExLangParser.MACRO_REF, 0); }
		public MacroReferenceExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macroReferenceExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).enterMacroReferenceExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExLangParserListener ) ((ExLangParserListener)listener).exitMacroReferenceExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExLangParserVisitor ) return ((ExLangParserVisitor<? extends T>)visitor).visitMacroReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MacroReferenceExpressionContext macroReferenceExpression() throws RecognitionException {
		MacroReferenceExpressionContext _localctx = new MacroReferenceExpressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_macroReferenceExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(MACRO_REF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\35\u0083\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\7\2\"\n\2\f\2"+
		"\16\2%\13\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3-\n\3\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\6\5\6\66\n\6\3\6\3\6\7\6:\n\6\f\6\16\6=\13\6\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\5\7E\n\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\7\f]\n\f\f\f\16\f`\13\f\3\r\3\r\3"+
		"\r\5\re\n\r\3\16\3\16\3\16\3\16\3\16\3\16\7\16m\n\16\f\16\16\16p\13\16"+
		"\3\16\3\16\3\17\3\17\3\17\3\17\7\17x\n\17\f\17\16\17{\13\17\5\17}\n\17"+
		"\3\17\3\17\3\20\3\20\3\20\2\2\21\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36"+
		"\2\2\2\u0086\2#\3\2\2\2\4,\3\2\2\2\6.\3\2\2\2\b\60\3\2\2\2\n\65\3\2\2"+
		"\2\fD\3\2\2\2\16F\3\2\2\2\20I\3\2\2\2\22L\3\2\2\2\24Q\3\2\2\2\26Y\3\2"+
		"\2\2\30d\3\2\2\2\32f\3\2\2\2\34s\3\2\2\2\36\u0080\3\2\2\2 \"\5\4\3\2!"+
		" \3\2\2\2\"%\3\2\2\2#!\3\2\2\2#$\3\2\2\2$&\3\2\2\2%#\3\2\2\2&\'\7\2\2"+
		"\3\'\3\3\2\2\2(-\5\6\4\2)-\5\f\7\2*-\5\b\5\2+-\5\n\6\2,(\3\2\2\2,)\3\2"+
		"\2\2,*\3\2\2\2,+\3\2\2\2-\5\3\2\2\2./\7\3\2\2/\7\3\2\2\2\60\61\7\13\2"+
		"\2\61\62\7\6\2\2\62\63\5\30\r\2\63\t\3\2\2\2\64\66\7\13\2\2\65\64\3\2"+
		"\2\2\65\66\3\2\2\2\66\67\3\2\2\2\67;\7\4\2\28:\5\4\3\298\3\2\2\2:=\3\2"+
		"\2\2;9\3\2\2\2;<\3\2\2\2<>\3\2\2\2=;\3\2\2\2>?\7\5\2\2?\13\3\2\2\2@E\5"+
		"\20\t\2AE\5\22\n\2BE\5\24\13\2CE\5\16\b\2D@\3\2\2\2DA\3\2\2\2DB\3\2\2"+
		"\2DC\3\2\2\2E\r\3\2\2\2FG\7\t\2\2GH\7\n\2\2H\17\3\2\2\2IJ\7\b\2\2JK\7"+
		"\32\2\2K\21\3\2\2\2LM\7\7\2\2MN\7\32\2\2NO\7\6\2\2OP\5\30\r\2P\23\3\2"+
		"\2\2QR\7\7\2\2RS\7\32\2\2ST\7\31\2\2TU\5\26\f\2UV\7\23\2\2VW\7\6\2\2W"+
		"X\5\30\r\2X\25\3\2\2\2Y^\7\32\2\2Z[\7\22\2\2[]\7\32\2\2\\Z\3\2\2\2]`\3"+
		"\2\2\2^\\\3\2\2\2^_\3\2\2\2_\27\3\2\2\2`^\3\2\2\2ae\5\32\16\2be\5\34\17"+
		"\2ce\5\36\20\2da\3\2\2\2db\3\2\2\2dc\3\2\2\2e\31\3\2\2\2fn\7\17\2\2gm"+
		"\7\25\2\2hm\7\26\2\2im\7\30\2\2jm\5\34\17\2km\5\36\20\2lg\3\2\2\2lh\3"+
		"\2\2\2li\3\2\2\2lj\3\2\2\2lk\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2oq\3"+
		"\2\2\2pn\3\2\2\2qr\7\27\2\2r\33\3\2\2\2s|\7\21\2\2ty\5\30\r\2uv\7\22\2"+
		"\2vx\5\30\r\2wu\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z}\3\2\2\2{y\3\2"+
		"\2\2|t\3\2\2\2|}\3\2\2\2}~\3\2\2\2~\177\7\23\2\2\177\35\3\2\2\2\u0080"+
		"\u0081\7\20\2\2\u0081\37\3\2\2\2\r#,\65;D^dlny|";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}