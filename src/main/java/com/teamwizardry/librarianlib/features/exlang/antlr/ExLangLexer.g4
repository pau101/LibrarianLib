lexer grammar ExLangLexer;

COMMENT : '//' .*? ('\n'| EOF);

BLOCK_BEGIN : '{';
BLOCK_END : '}';
EQUALS : '=' -> pushMode(EXPRESSION);

DEFINE : '#define' -> mode(DEFINE_ARGS);
UNDEFINE : '#undefine' -> mode(IDENTIFIER_ONLY);
IMPORT : '#import';

STRING : SINGLE_QUOTED_TEXT | DOUBLE_QUOTED_TEXT;

LANGUAGE_KEY : [a-zA-Z0-9._]+;
ROOT_WHITESPACE : (WS | NL)+ -> skip;

mode IDENTIFIER_ONLY;

IDENTIFIER_ONLY_WHITESPACE : (WS | NL)+ -> skip;
IDENTIFIER_ONLY_IDENTIFIER : IDENTIFIER_FRAGMENT -> type(IDENTIFIER), mode(DEFAULT_MODE);

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
ESCAPE_UNICODE : '\\u' [0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F];
BARE_MACRO_PREFIX: '$' -> type(TEXT);
STRING_END: '"' -> popMode;
D_MACRO_REF : '$' (IDENTIFIER_FRAGMENT | '{' IDENTIFIER_FRAGMENT '}') -> type(MACRO_REF);
D_MACRO_CALL : '$' IDENTIFIER_FRAGMENT '(' -> type(MACRO_CALL), pushMode(MACRO_ARGS), pushMode(EXPRESSION);
TEXT : ~('\\' | '$' | '"')+;

mode SINGLE_QUOTE_STRING;

S_ESCAPE_SEQUENCE : '\\' . -> type(ESCAPE_SEQUENCE);
S_BARE_MACRO_PREFIX: '$' -> type(TEXT);
S_STRING_END: '\'' -> type(STRING_END), popMode;
S_MACRO_REF : '$' (IDENTIFIER_FRAGMENT | '{' IDENTIFIER_FRAGMENT '}') -> type(MACRO_REF);
S_MACRO_CALL : '$' IDENTIFIER_FRAGMENT '(' -> type(MACRO_CALL), pushMode(MACRO_ARGS), pushMode(EXPRESSION);
S_TEXT : ~('\\' | '$' | '\'')+ -> type(TEXT);

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
