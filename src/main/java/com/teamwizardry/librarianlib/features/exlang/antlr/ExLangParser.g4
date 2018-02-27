parser grammar ExLangParser;

options { tokenVocab=ExLangLexer; }

file        : element* ;

element : (comment | directive | entry | block);

comment: COMMENT;

entry: LANGUAGE_KEY EQUALS expression;

block: LANGUAGE_KEY BLOCK_BEGIN element* BLOCK_END;

directive: defineVarDirective | defineFuncDirective | importDirective;

importDirective: IMPORT expression;
defineVarDirective: DEFINE IDENTIFIER EQUALS expression;
defineFuncDirective: DEFINE IDENTIFIER PARAMS_BEGIN defineParams PARAMS_END EQUALS expression;
defineParams: (IDENTIFIER (PARAM_SEPARATOR IDENTIFIER)*)?;

expression: ((string | macroReference | macroCall) (PLUS (string | macroReference | macroCall))*);//(macroReference | macroCall | string);

macroReference: MACRO_REF;
macroCall: MACRO_REF PARAMS_BEGIN (expression (PARAM_SEPARATOR expression)*)? PARAMS_END;

string: STRING;
