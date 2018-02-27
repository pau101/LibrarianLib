lexer grammar ExLangLexer;

COMMENT : '//' .*? ('\n'| EOF);

BLOCK_BEGIN : '{';
BLOCK_END : '}';
EQUALS : '=';
PLUS : '+';

DEFINE : '#define' -> mode(DEFINE_ARGS);
IMPORT : '#import';

LANGUAGE_KEY : [a-zA-Z0-9._]+;
ROOT_WHITESPACE : (WS | NL)+ -> skip;

STRING : SINGLE_QUOTED_TEXT | DOUBLE_QUOTED_TEXT;
MACRO_REF : '$' IDENTIFIER_FRAGMENT;

PARAMS_BEGIN : '(';
PARAM_SEPARATOR : ',';
PARAMS_END : ')';

mode DEFINE_ARGS;

DEFINE_PARAMS_BEGIN : '(' -> type(PARAMS_BEGIN);
DEFINE_PARAMS_END : ')' -> type(PARAMS_END), mode(DEFAULT_MODE);
IDENTIFIER : IDENTIFIER_FRAGMENT;
DEFINE_PARAM_SEPARATOR : ',' -> type(PARAM_SEPARATOR);
DEFINE_WHITESPACE : (WS | NL)+ -> skip;
DEFINE_EQUALS : '=' -> type(EQUALS), mode(DEFAULT_MODE);

fragment LETTER : ('a'..'z' | 'A'..'Z');
fragment DIGIT : ('0'..'9');
fragment IDENTIFIER_FRAGMENT : LETTER (DIGIT | LETTER)*;
fragment WS : (' ' | '\t');
fragment NL: ('\r\n' | '\r' | '\n');
fragment SINGLE_QUOTED_TEXT : '\'' ('\\\\' | '\\\'' | .)*? '\'';
fragment DOUBLE_QUOTED_TEXT : '"' ('\\\\' | '\\"' | .)*? '"';
