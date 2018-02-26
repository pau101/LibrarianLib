package com.teamwizardry.librarianlib.features.exlang.lexer;

import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;
import net.minecraft.util.ResourceLocation;
import com.teamwizardry.librarianlib.features.exlang.parser.LexerException;
/**
 * This class is a simple example lexer.
 */
@SuppressWarnings("all")
%%

%public
%class ExLangLexer
%unicode
%type ExLangToken
%line
%column

%{
    public ResourceLocation file = new ResourceLocation("missingno");

    private ExLangToken lastSuccessfulToken = null;
    private ExLangToken peekedToken = null;

    public @NotNull ExLangToken nextToken() throws java.io.IOException {
        if(peekedToken != null) {
            ExLangToken token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            lastSuccessfulToken = errorLex();
            return lastSuccessfulToken;
        }
    }

    public @NotNull ExLangToken peekToken() throws java.io.IOException {
        if(peekedToken == null) {
            peekedToken = errorLex();
            lastSuccessfulToken = peekedToken;
        }
        return peekedToken;
    }

    private ExLangToken errorLex() {
        try {
            return yylex();
        } catch (Throwable e) {
            throw new LexerException("Error getting next token.", lastSuccessfulToken, e);
        }
    }

    private LinkedList<Integer> stateStack = new LinkedList<>();

    private ExLangToken symbol(ExLangSymbol type) {
        return new ExLangToken(type, file, yyline, yycolumn, "");
    }
    private ExLangToken symbol(ExLangSymbol type, String value) {
        return new ExLangToken(type, file, yyline, yycolumn, value);
    }

    private void pushState() {
        stateStack.push(yystate());
    }

    private void pushState(int state) {
        stateStack.push(state);
    }

    private void popState() {
        if(!stateStack.isEmpty()) {
            yybegin(stateStack.pop());
        }
    }

    private boolean _expressionEndOnComma = false;
%}

%eofval{
    return symbol(ExLangSymbol.BLOCK_END);
%eofval}

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment   = "/*" ([^*]|\*[^/])* ~"*/"
EndOfLineComment     = "//" [^\R]* \R?

Identifier = [:jletter:] [:jletterdigit:]*

LanguageKey = [\w.]+ //TODO: Verify this regex

%state MACRO_DEFINITION_IDENTIFIER, MACRO_DEFINITION_PARAMS_OR_BEGIN, MACRO_DEFINITION_PARAM,
%state MACRO_DEFINITION_NEXT_PARAM_OR_END, MACRO_DEFINITION_BEGIN

%state LANG_BLOCK_OR_VALUE

%xstate EXPRESSION, BARE_EXPRESSION, DOUBLE_QUOTED_EXPRESSION, SINGLE_QUOTED_EXPRESSION

%xstate MACRO_REFERENCE_PARAMS_OR_END
%state MACRO_REFERENCE_NEXT_PARAM_OR_END

%state PATH_BEGIN
%xstate PATH_DOUBLE_QUOTE, PATH_SINGLE_QUOTE

%%

[\R\s] { /* ignore */ }

{Comment} { /* ignore */ }

// =============================================== Import definition ================================================ //

<YYINITIAL>"#import" {
    yybegin(PATH_BEGIN);
    return symbol(ExLangSymbol.IMPORT_BEGIN);
}

<PATH_BEGIN> {
    "\"" {
        yybegin(PATH_DOUBLE_QUOTE);
    }
    "'" {
        yybegin(PATH_SINGLE_QUOTE);
    }
}
<PATH_DOUBLE_QUOTE> {
    [^\n\r\"] {
        return symbol(ExLangSymbol.PATH_COMPONENT, yytext());
    }
    "\"" {
        yybegin(YYINITIAL);
        return symbol(ExLangSymbol.PATH_END);
    }
}
<PATH_SINGLE_QUOTE> {
    [^\n\r'] {
        return symbol(ExLangSymbol.PATH_COMPONENT, yytext());
    }
    "'" {
        yybegin(YYINITIAL);
        return symbol(ExLangSymbol.PATH_END);
    }
}

// ====================================== Macro name and parameter definition ======================================= //

// defining a new macro
<YYINITIAL>"#define" {
    yybegin(MACRO_DEFINITION_IDENTIFIER);
    return symbol(ExLangSymbol.MACRO_DEFINE);
}
// naming the macro
<MACRO_DEFINITION_IDENTIFIER>{Identifier} {
    yybegin(MACRO_DEFINITION_PARAMS_OR_BEGIN);
    return symbol(ExLangSymbol.IDENTIFIER, yytext());
}
// deciding if the macro is a variable or a function
<MACRO_DEFINITION_PARAMS_OR_BEGIN, MACRO_DEFINITION_BEGIN> {
    "=" {
        pushState(YYINITIAL);
        yybegin(EXPRESSION);
    }
}
<MACRO_DEFINITION_PARAMS_OR_BEGIN> {
    "(" {
        yybegin(MACRO_DEFINITION_PARAM);
        return symbol(ExLangSymbol.MACRO_DEFINITION_PARAMS_BEGIN);
    }
}
<MACRO_DEFINITION_PARAM>{Identifier} {
    yybegin(MACRO_DEFINITION_NEXT_PARAM_OR_END);
    return symbol(ExLangSymbol.IDENTIFIER, yytext());
}
<MACRO_DEFINITION_NEXT_PARAM_OR_END> {
    "," {
        yybegin(MACRO_DEFINITION_PARAM);
    }
    ")" {
        yybegin(MACRO_DEFINITION_BEGIN);
        return symbol(ExLangSymbol.MACRO_DEFINITION_PARAMS_END);
    }
}

// ============================================== Lang key definition =============================================== //
<YYINITIAL>{LanguageKey} {
    yybegin(LANG_BLOCK_OR_VALUE);
    return symbol(ExLangSymbol.LANG_KEY, yytext());
}
<LANG_BLOCK_OR_VALUE> {
    "{" {
        yybegin(YYINITIAL); // blocks parse identically to the root, any difference is handled in the parser
        return symbol(ExLangSymbol.BLOCK_BEGIN);
    }
    "=" {
        pushState(YYINITIAL);
        yybegin(EXPRESSION);
        return symbol(ExLangSymbol.EXPRESSION_BEGIN);
    }
}
<YYINITIAL> {
    "{" {
        yybegin(YYINITIAL); // blocks parse identically to the root, any difference is handled in the parser
        return symbol(ExLangSymbol.BLOCK_BEGIN);
    }
}

// ============================================= Expression definition ============================================== //

<EXPRESSION> {
    "\"" {
        yybegin(DOUBLE_QUOTED_EXPRESSION);
        return symbol(ExLangSymbol.EXPRESSION_BEGIN);
    }
    "'" {
        yybegin(SINGLE_QUOTED_EXPRESSION);
        return symbol(ExLangSymbol.EXPRESSION_BEGIN);
    }
    \s+ { /* ignore */ }
    [^] {
        yypushback(1);
        yybegin(BARE_EXPRESSION);
        return symbol(ExLangSymbol.EXPRESSION_BEGIN);
    }
}

<BARE_EXPRESSION, DOUBLE_QUOTED_EXPRESSION, SINGLE_QUOTED_EXPRESSION>{
    \\[^\n\r] { return symbol(ExLangSymbol.ESCAPED_CHARACTER, yytext().substring(1)); }
    \\u[0-9a-fA-F]{4} { return symbol(ExLangSymbol.ESCAPED_CODEPOINT, yytext().substring(2)); }
    "$" { return symbol(ExLangSymbol.STRING, "$"); }
    "$" {Identifier} {
        pushState();
        yybegin(MACRO_REFERENCE_PARAMS_OR_END);
        return symbol(ExLangSymbol.MACRO_REFERENCE, yytext().substring(1));
    }
    "${" {Identifier} "}" {
        pushState();
        yybegin(MACRO_REFERENCE_PARAMS_OR_END);
        return symbol(ExLangSymbol.MACRO_REFERENCE, yytext().substring(2, yytext().length()-1));
    }
    "," {
        if(_expressionEndOnComma) {
            _expressionEndOnComma = false;
            yypushback(1);

            popState();
            return symbol(ExLangSymbol.EXPRESSION_END);
        } else {
            return symbol(ExLangSymbol.STRING, ",");
        }
    }
}
<BARE_EXPRESSION> {
    \\\R \s+ { return symbol(ExLangSymbol.STRING, yytext().replaceAll("\n\\s+", "\n")); }
    [^\n\r$,\\]+ { return symbol(ExLangSymbol.STRING, yytext()); }
    \R {
        _expressionEndOnComma = false;

        popState();
        return symbol(ExLangSymbol.EXPRESSION_END);
    }
}
<DOUBLE_QUOTED_EXPRESSION> {
    \\\R \s+ { return symbol(ExLangSymbol.STRING, yytext().replaceAll("\n\\s+", "")); }
    \R \s+ { return symbol(ExLangSymbol.STRING, yytext().replaceAll("\n\\s+", "\n")); }
    [^\n\r$,\"\\]+ { return symbol(ExLangSymbol.STRING, yytext()); }
    "\"" {
        _expressionEndOnComma = false;

        popState();
        return symbol(ExLangSymbol.EXPRESSION_END);
    }
}
<SINGLE_QUOTED_EXPRESSION> {
    \\\R \s+ { return symbol(ExLangSymbol.STRING, yytext().replaceAll("\n\\s+", "")); }
    \R \s+ { return symbol(ExLangSymbol.STRING, yytext().replaceAll("\n\\s+", "\n")); }
    [^\n\r$,'\\]+ { return symbol(ExLangSymbol.STRING, yytext()); }
    "'" {
        _expressionEndOnComma = false;

        popState();
        return symbol(ExLangSymbol.EXPRESSION_END);
     }
}

// ================================================ Macro references ================================================ //

<MACRO_REFERENCE_PARAMS_OR_END> {
    \s* "(" {
        pushState(MACRO_REFERENCE_NEXT_PARAM_OR_END);

        _expressionEndOnComma = true;
        yybegin(EXPRESSION);
        return symbol(ExLangSymbol.MACRO_REFERENCE_PARAMS_BEGIN);
    }
    [^] {
        popState();
    }
}
<MACRO_REFERENCE_NEXT_PARAM_OR_END> {
    "," {
        pushState();

        _expressionEndOnComma = true;
        yybegin(EXPRESSION);
    }
    ")" {
        popState();
        return symbol(ExLangSymbol.MACRO_REFERENCE_PARAMS_END);
    }
}

// =================================================== Whitespace =================================================== //

[\R\s] { /* ignore */ }
