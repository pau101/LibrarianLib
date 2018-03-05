// Generated from /home/code/Documents/mods/LibrarianLib/src/main/java/com/teamwizardry/librarianlib/features/exlang/antlr/ExLangLexer.g4 by ANTLR 4.7
package com.teamwizardry.librarianlib.features.exlang.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExLangLexer extends Lexer {
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
		IDENTIFIER_ONLY=1, EXPRESSION=2, MACRO_ARGS=3, DOUBLE_QUOTE_STRING=4, 
		SINGLE_QUOTE_STRING=5, DEFINE_ARGS=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "IDENTIFIER_ONLY", "EXPRESSION", "MACRO_ARGS", "DOUBLE_QUOTE_STRING", 
		"SINGLE_QUOTE_STRING", "DEFINE_ARGS"
	};

	public static final String[] ruleNames = {
		"COMMENT", "BLOCK_BEGIN", "BLOCK_END", "EQUALS", "DEFINE", "UNDEFINE", 
		"IMPORT", "STRING", "LANGUAGE_KEY", "ROOT_WHITESPACE", "IDENTIFIER_ONLY_WHITESPACE", 
		"IDENTIFIER_ONLY_IDENTIFIER", "EXPRESSION_WHITESPACE", "STRING_BEGIN", 
		"STRING_BEGIN_SINGLE", "MACRO_REF", "MACRO_CALL", "PARAM_SEPARATOR", "PARAMS_END", 
		"MACRO_ARGS_WHITESPACE", "ESCAPE_SEQUENCE", "ESCAPE_UNICODE", "BARE_MACRO_PREFIX", 
		"STRING_END", "D_MACRO_REF", "D_MACRO_CALL", "TEXT", "S_ESCAPE_SEQUENCE", 
		"S_BARE_MACRO_PREFIX", "S_STRING_END", "S_MACRO_REF", "S_MACRO_CALL", 
		"S_TEXT", "PARAMS_BEGIN", "DEFINE_PARAMS_END", "IDENTIFIER", "DEFINE_PARAM_SEPARATOR", 
		"DEFINE_WHITESPACE", "DEFINE_EQUALS", "LETTER", "DIGIT", "IDENTIFIER_FRAGMENT", 
		"WS", "NL", "SINGLE_QUOTED_TEXT", "DOUBLE_QUOTED_TEXT"
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


	public ExLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ExLangLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\35\u017b\b\1\b\1"+
		"\b\1\b\1\b\1\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4"+
		"\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4"+
		"\20\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4"+
		"\27\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4"+
		"\36\t\36\4\37\t\37\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'"+
		"\4(\t(\4)\t)\4*\t*\4+\t+\4,\t,\4-\t-\4.\t.\4/\t/\3\2\3\2\3\2\3\2\7\2j"+
		"\n\2\f\2\16\2m\13\2\3\2\5\2p\n\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\5\t\u009a\n\t\3\n"+
		"\6\n\u009d\n\n\r\n\16\n\u009e\3\13\3\13\6\13\u00a3\n\13\r\13\16\13\u00a4"+
		"\3\13\3\13\3\f\3\f\6\f\u00ab\n\f\r\f\16\f\u00ac\3\f\3\f\3\r\3\r\3\r\3"+
		"\r\3\r\3\16\3\16\6\16\u00b8\n\16\r\16\16\16\u00b9\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\6\25\u00dd\n\25\r\25\16\25\u00de\3\25\3\25\3\26\3\26\3\26\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3"+
		"\31\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u00fc\n\32\3\32\3\32\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\34\6\34\u0109\n\34\r\34\16\34\u010a\3"+
		"\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3"+
		" \3 \3 \3 \3 \3 \5 \u0121\n \3 \3 \3!\3!\3!\3!\3!\3!\3!\3!\3\"\6\"\u012e"+
		"\n\"\r\"\16\"\u012f\3\"\3\"\3#\3#\3$\3$\3$\3$\3$\3%\3%\3&\3&\3&\3&\3\'"+
		"\3\'\6\'\u0143\n\'\r\'\16\'\u0144\3\'\3\'\3(\3(\3(\3(\3(\3(\3)\3)\3*\3"+
		"*\3+\3+\3+\7+\u0156\n+\f+\16+\u0159\13+\3,\3,\3-\3-\3-\5-\u0160\n-\3."+
		"\3.\3.\3.\3.\3.\7.\u0168\n.\f.\16.\u016b\13.\3.\3.\3/\3/\3/\3/\3/\3/\7"+
		"/\u0175\n/\f/\16/\u0178\13/\3/\3/\5k\u0169\u0176\2\60\t\3\13\4\r\5\17"+
		"\6\21\7\23\b\25\t\27\n\31\13\33\f\35\r\37\2!\16#\17%\2\'\20)\21+\22-\23"+
		"/\24\61\25\63\26\65\34\67\279\2;\2=\30?\2A\2C\35E\2G\2I\2K\31M\2O\32Q"+
		"\2S\33U\2W\2Y\2[\2]\2_\2a\2c\2\t\2\3\4\5\6\7\b\n\3\3\f\f\7\2\60\60\62"+
		";C\\aac|\5\2\62;CHch\5\2$$&&^^\5\2&&))^^\4\2C\\c|\4\2\13\13\"\"\4\2\f"+
		"\f\17\17\2\u0187\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\3\35\3\2\2\2\3\37\3\2\2\2\4!\3\2\2\2\4#\3\2\2\2\4%\3\2\2\2\4\'\3"+
		"\2\2\2\4)\3\2\2\2\5+\3\2\2\2\5-\3\2\2\2\5/\3\2\2\2\6\61\3\2\2\2\6\63\3"+
		"\2\2\2\6\65\3\2\2\2\6\67\3\2\2\2\69\3\2\2\2\6;\3\2\2\2\6=\3\2\2\2\7?\3"+
		"\2\2\2\7A\3\2\2\2\7C\3\2\2\2\7E\3\2\2\2\7G\3\2\2\2\7I\3\2\2\2\bK\3\2\2"+
		"\2\bM\3\2\2\2\bO\3\2\2\2\bQ\3\2\2\2\bS\3\2\2\2\bU\3\2\2\2\te\3\2\2\2\13"+
		"q\3\2\2\2\rs\3\2\2\2\17u\3\2\2\2\21y\3\2\2\2\23\u0083\3\2\2\2\25\u008f"+
		"\3\2\2\2\27\u0099\3\2\2\2\31\u009c\3\2\2\2\33\u00a2\3\2\2\2\35\u00aa\3"+
		"\2\2\2\37\u00b0\3\2\2\2!\u00b7\3\2\2\2#\u00bd\3\2\2\2%\u00c1\3\2\2\2\'"+
		"\u00c6\3\2\2\2)\u00cb\3\2\2\2+\u00d2\3\2\2\2-\u00d6\3\2\2\2/\u00dc\3\2"+
		"\2\2\61\u00e2\3\2\2\2\63\u00e5\3\2\2\2\65\u00ed\3\2\2\2\67\u00f1\3\2\2"+
		"\29\u00f5\3\2\2\2;\u00ff\3\2\2\2=\u0108\3\2\2\2?\u010c\3\2\2\2A\u0111"+
		"\3\2\2\2C\u0115\3\2\2\2E\u011a\3\2\2\2G\u0124\3\2\2\2I\u012d\3\2\2\2K"+
		"\u0133\3\2\2\2M\u0135\3\2\2\2O\u013a\3\2\2\2Q\u013c\3\2\2\2S\u0142\3\2"+
		"\2\2U\u0148\3\2\2\2W\u014e\3\2\2\2Y\u0150\3\2\2\2[\u0152\3\2\2\2]\u015a"+
		"\3\2\2\2_\u015f\3\2\2\2a\u0161\3\2\2\2c\u016e\3\2\2\2ef\7\61\2\2fg\7\61"+
		"\2\2gk\3\2\2\2hj\13\2\2\2ih\3\2\2\2jm\3\2\2\2kl\3\2\2\2ki\3\2\2\2lo\3"+
		"\2\2\2mk\3\2\2\2np\t\2\2\2on\3\2\2\2p\n\3\2\2\2qr\7}\2\2r\f\3\2\2\2st"+
		"\7\177\2\2t\16\3\2\2\2uv\7?\2\2vw\3\2\2\2wx\b\5\2\2x\20\3\2\2\2yz\7%\2"+
		"\2z{\7f\2\2{|\7g\2\2|}\7h\2\2}~\7k\2\2~\177\7p\2\2\177\u0080\7g\2\2\u0080"+
		"\u0081\3\2\2\2\u0081\u0082\b\6\3\2\u0082\22\3\2\2\2\u0083\u0084\7%\2\2"+
		"\u0084\u0085\7w\2\2\u0085\u0086\7p\2\2\u0086\u0087\7f\2\2\u0087\u0088"+
		"\7g\2\2\u0088\u0089\7h\2\2\u0089\u008a\7k\2\2\u008a\u008b\7p\2\2\u008b"+
		"\u008c\7g\2\2\u008c\u008d\3\2\2\2\u008d\u008e\b\7\4\2\u008e\24\3\2\2\2"+
		"\u008f\u0090\7%\2\2\u0090\u0091\7k\2\2\u0091\u0092\7o\2\2\u0092\u0093"+
		"\7r\2\2\u0093\u0094\7q\2\2\u0094\u0095\7t\2\2\u0095\u0096\7v\2\2\u0096"+
		"\26\3\2\2\2\u0097\u009a\5a.\2\u0098\u009a\5c/\2\u0099\u0097\3\2\2\2\u0099"+
		"\u0098\3\2\2\2\u009a\30\3\2\2\2\u009b\u009d\t\3\2\2\u009c\u009b\3\2\2"+
		"\2\u009d\u009e\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\32"+
		"\3\2\2\2\u00a0\u00a3\5],\2\u00a1\u00a3\5_-\2\u00a2\u00a0\3\2\2\2\u00a2"+
		"\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2"+
		"\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\b\13\5\2\u00a7\34\3\2\2\2\u00a8\u00ab"+
		"\5],\2\u00a9\u00ab\5_-\2\u00aa\u00a8\3\2\2\2\u00aa\u00a9\3\2\2\2\u00ab"+
		"\u00ac\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\3\2"+
		"\2\2\u00ae\u00af\b\f\5\2\u00af\36\3\2\2\2\u00b0\u00b1\5[+\2\u00b1\u00b2"+
		"\3\2\2\2\u00b2\u00b3\b\r\6\2\u00b3\u00b4\b\r\7\2\u00b4 \3\2\2\2\u00b5"+
		"\u00b8\5],\2\u00b6\u00b8\5_-\2\u00b7\u00b5\3\2\2\2\u00b7\u00b6\3\2\2\2"+
		"\u00b8\u00b9\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bb"+
		"\3\2\2\2\u00bb\u00bc\b\16\5\2\u00bc\"\3\2\2\2\u00bd\u00be\7$\2\2\u00be"+
		"\u00bf\3\2\2\2\u00bf\u00c0\b\17\b\2\u00c0$\3\2\2\2\u00c1\u00c2\7)\2\2"+
		"\u00c2\u00c3\3\2\2\2\u00c3\u00c4\b\20\t\2\u00c4\u00c5\b\20\n\2\u00c5&"+
		"\3\2\2\2\u00c6\u00c7\7&\2\2\u00c7\u00c8\5[+\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00ca\b\21\13\2\u00ca(\3\2\2\2\u00cb\u00cc\7&\2\2\u00cc\u00cd\5[+\2\u00cd"+
		"\u00ce\7*\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\b\22\f\2\u00d0\u00d1\b\22"+
		"\2\2\u00d1*\3\2\2\2\u00d2\u00d3\7.\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d5"+
		"\b\23\2\2\u00d5,\3\2\2\2\u00d6\u00d7\7+\2\2\u00d7\u00d8\3\2\2\2\u00d8"+
		"\u00d9\b\24\13\2\u00d9.\3\2\2\2\u00da\u00dd\5],\2\u00db\u00dd\5_-\2\u00dc"+
		"\u00da\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00dc\3\2"+
		"\2\2\u00de\u00df\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e1\b\25\5\2\u00e1"+
		"\60\3\2\2\2\u00e2\u00e3\7^\2\2\u00e3\u00e4\13\2\2\2\u00e4\62\3\2\2\2\u00e5"+
		"\u00e6\7^\2\2\u00e6\u00e7\7w\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00e9\t\4\2"+
		"\2\u00e9\u00ea\t\4\2\2\u00ea\u00eb\t\4\2\2\u00eb\u00ec\t\4\2\2\u00ec\64"+
		"\3\2\2\2\u00ed\u00ee\7&\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\b\30\r\2\u00f0"+
		"\66\3\2\2\2\u00f1\u00f2\7$\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\b\31\13"+
		"\2\u00f48\3\2\2\2\u00f5\u00fb\7&\2\2\u00f6\u00fc\5[+\2\u00f7\u00f8\7}"+
		"\2\2\u00f8\u00f9\5[+\2\u00f9\u00fa\7\177\2\2\u00fa\u00fc\3\2\2\2\u00fb"+
		"\u00f6\3\2\2\2\u00fb\u00f7\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00fe\b\32"+
		"\16\2\u00fe:\3\2\2\2\u00ff\u0100\7&\2\2\u0100\u0101\5[+\2\u0101\u0102"+
		"\7*\2\2\u0102\u0103\3\2\2\2\u0103\u0104\b\33\17\2\u0104\u0105\b\33\20"+
		"\2\u0105\u0106\b\33\2\2\u0106<\3\2\2\2\u0107\u0109\n\5\2\2\u0108\u0107"+
		"\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b"+
		">\3\2\2\2\u010c\u010d\7^\2\2\u010d\u010e\13\2\2\2\u010e\u010f\3\2\2\2"+
		"\u010f\u0110\b\35\21\2\u0110@\3\2\2\2\u0111\u0112\7&\2\2\u0112\u0113\3"+
		"\2\2\2\u0113\u0114\b\36\r\2\u0114B\3\2\2\2\u0115\u0116\7)\2\2\u0116\u0117"+
		"\3\2\2\2\u0117\u0118\b\37\22\2\u0118\u0119\b\37\13\2\u0119D\3\2\2\2\u011a"+
		"\u0120\7&\2\2\u011b\u0121\5[+\2\u011c\u011d\7}\2\2\u011d\u011e\5[+\2\u011e"+
		"\u011f\7\177\2\2\u011f\u0121\3\2\2\2\u0120\u011b\3\2\2\2\u0120\u011c\3"+
		"\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\b \16\2\u0123F\3\2\2\2\u0124\u0125"+
		"\7&\2\2\u0125\u0126\5[+\2\u0126\u0127\7*\2\2\u0127\u0128\3\2\2\2\u0128"+
		"\u0129\b!\17\2\u0129\u012a\b!\20\2\u012a\u012b\b!\2\2\u012bH\3\2\2\2\u012c"+
		"\u012e\n\6\2\2\u012d\u012c\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u012d\3\2"+
		"\2\2\u012f\u0130\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0132\b\"\r\2\u0132"+
		"J\3\2\2\2\u0133\u0134\7*\2\2\u0134L\3\2\2\2\u0135\u0136\7+\2\2\u0136\u0137"+
		"\3\2\2\2\u0137\u0138\b$\23\2\u0138\u0139\b$\7\2\u0139N\3\2\2\2\u013a\u013b"+
		"\5[+\2\u013bP\3\2\2\2\u013c\u013d\7.\2\2\u013d\u013e\3\2\2\2\u013e\u013f"+
		"\b&\24\2\u013fR\3\2\2\2\u0140\u0143\5],\2\u0141\u0143\5_-\2\u0142\u0140"+
		"\3\2\2\2\u0142\u0141\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0142\3\2\2\2\u0144"+
		"\u0145\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0147\b\'\5\2\u0147T\3\2\2\2"+
		"\u0148\u0149\7?\2\2\u0149\u014a\3\2\2\2\u014a\u014b\b(\25\2\u014b\u014c"+
		"\b(\7\2\u014c\u014d\b(\2\2\u014dV\3\2\2\2\u014e\u014f\t\7\2\2\u014fX\3"+
		"\2\2\2\u0150\u0151\4\62;\2\u0151Z\3\2\2\2\u0152\u0157\5W)\2\u0153\u0156"+
		"\5Y*\2\u0154\u0156\5W)\2\u0155\u0153\3\2\2\2\u0155\u0154\3\2\2\2\u0156"+
		"\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158\\\3\2\2\2"+
		"\u0159\u0157\3\2\2\2\u015a\u015b\t\b\2\2\u015b^\3\2\2\2\u015c\u015d\7"+
		"\17\2\2\u015d\u0160\7\f\2\2\u015e\u0160\t\t\2\2\u015f\u015c\3\2\2\2\u015f"+
		"\u015e\3\2\2\2\u0160`\3\2\2\2\u0161\u0169\7)\2\2\u0162\u0163\7^\2\2\u0163"+
		"\u0168\7^\2\2\u0164\u0165\7^\2\2\u0165\u0168\7)\2\2\u0166\u0168\13\2\2"+
		"\2\u0167\u0162\3\2\2\2\u0167\u0164\3\2\2\2\u0167\u0166\3\2\2\2\u0168\u016b"+
		"\3\2\2\2\u0169\u016a\3\2\2\2\u0169\u0167\3\2\2\2\u016a\u016c\3\2\2\2\u016b"+
		"\u0169\3\2\2\2\u016c\u016d\7)\2\2\u016db\3\2\2\2\u016e\u0176\7$\2\2\u016f"+
		"\u0170\7^\2\2\u0170\u0175\7^\2\2\u0171\u0172\7^\2\2\u0172\u0175\7$\2\2"+
		"\u0173\u0175\13\2\2\2\u0174\u016f\3\2\2\2\u0174\u0171\3\2\2\2\u0174\u0173"+
		"\3\2\2\2\u0175\u0178\3\2\2\2\u0176\u0177\3\2\2\2\u0176\u0174\3\2\2\2\u0177"+
		"\u0179\3\2\2\2\u0178\u0176\3\2\2\2\u0179\u017a\7$\2\2\u017ad\3\2\2\2\""+
		"\2\3\4\5\6\7\bko\u0099\u009e\u00a2\u00a4\u00aa\u00ac\u00b7\u00b9\u00dc"+
		"\u00de\u00fb\u010a\u0120\u012f\u0142\u0144\u0155\u0157\u015f\u0167\u0169"+
		"\u0174\u0176\26\7\4\2\4\b\2\4\3\2\b\2\2\t\32\2\4\2\2\4\6\2\t\17\2\4\7"+
		"\2\6\2\2\4\5\2\t\30\2\t\20\2\t\21\2\7\5\2\t\25\2\t\27\2\t\23\2\t\22\2"+
		"\t\6\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}