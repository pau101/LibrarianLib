lexer grammar ExLangLexer;

COMMENT : '//' .*? ('\n'| EOF);

BLOCK_BEGIN : '{';
BLOCK_END : '}';
EQUALS : '=' -> pushMode(EXPRESSION);

DEFINE : '#define' -> mode(DEFINE_ARGS);
IMPORT : '#import' -> pushMode(EXPRESSION);

LANGUAGE_KEY : [a-zA-Z0-9._]+;
ROOT_WHITESPACE : (WS | NL)+ -> skip;

mode EXPRESSION;

EXPRESSION_WHITESPACE : (WS | NL)+ -> skip;
STRING_BEGIN : '"' -> mode(DOUBLE_QUOTE_STRING);
STRING_BEGIN_SINGLE : '\'' -> type(STRING_BEGIN), mode(SINGLE_QUOTE_STRING);

MACRO_REF : '$' IDENTIFIER_FRAGMENT -> popMode;
MACRO_CALL : '$' IDENTIFIER_FRAGMENT '(' -> mode(MACRO_ARGS), pushMode(EXPRESSION);

mode MACRO_ARGS;

PARAM_SEPARATOR : ',' -> pushMode(EXPRESSION);
PARAMS_END : ')' -> popMode;
MACRO_ARGS_WHITESPACE : (WS | NL)+ -> skip;

mode DOUBLE_QUOTE_STRING;

ESCAPE_SEQUENCE : '\\' .;
BARE_MACRO_PREFIX: '$' -> type(TEXT);
STRING_END: '"' -> popMode;
D_MACRO_REF : '$' IDENTIFIER_FRAGMENT -> type(MACRO_REF);
D_MACRO_CALL : '$' IDENTIFIER_FRAGMENT '(' -> type(MACRO_CALL), pushMode(MACRO_ARGS), pushMode(EXPRESSION);
TEXT : ~('\\' | '$' | '"')+;

mode SINGLE_QUOTE_STRING;

S_ESCAPE_SEQUENCE : '\\' . -> type(ESCAPE_SEQUENCE);
S_BARE_MACRO_PREFIX: '$' -> type(TEXT);
S_STRING_END: '\'' -> type(STRING_END), popMode;
S_MACRO_REF : '$' IDENTIFIER_FRAGMENT -> type(MACRO_REF);
S_MACRO_CALL : '$' IDENTIFIER_FRAGMENT '(' -> type(MACRO_CALL), pushMode(MACRO_ARGS), pushMode(EXPRESSION);
S_TEXT : ~('\\' | '$' | '\'')+ -> type(TEXT);

//mode DOUBLE_QUOTE_STRING;
//
//D_STRING_ESCAPE_SEQUENCE : '\\' . -> type(ESCAPE_SEQUENCE);
//D_BARE_MACRO_PREFIX: '$' -> type(BARE_MACRO_PREFIX);
//D_STRING_END: '"' -> type(STRING_END), popMode;
//D_MACRO_INTERPOLATE : '$' IDENTIFIER_FRAGMENT -> type(MACRO_INTERPOLATE);
////D_MACRO_FUNCTION_INTERPOLATE : '$' IDENTIFIER_FRAGMENT '(' -> type(MACRO_FUNCTION_INTERPOLATE), pushMode(EXPRESSION);
//D_TEXT : ~('\\' | '$' | '"')+ -> type(TEXT);

mode DEFINE_ARGS;

PARAMS_BEGIN : '(';
DEFINE_PARAMS_END : ')' -> type(PARAMS_END), mode(DEFAULT_MODE);
IDENTIFIER : IDENTIFIER_FRAGMENT;
DEFINE_PARAM_SEPARATOR : ',' -> type(PARAM_SEPARATOR);
DEFINE_WHITESPACE : (WS | NL)+ -> skip;
DEFINE_EQUALS : '=' -> type(EQUALS), mode(DEFAULT_MODE), pushMode(EXPRESSION);

fragment LETTER : ('a'..'z' | 'A'..'Z');
fragment DIGIT : ('0'..'9');
fragment IDENTIFIER_FRAGMENT : LETTER (DIGIT | LETTER)*;
fragment WS : (' ' | '\t');
fragment NL: ('\r\n' | '\r' | '\n');
fragment SINGLE_QUOTED_TEXT : '\'' ('\\\\' | '\\\'' | .)*? '\'';
fragment DOUBLE_QUOTED_TEXT : '"' ('\\\\' | '\\"' | .)*? '"';
