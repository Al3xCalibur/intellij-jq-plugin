package com.github.al3xcalibur.jqtool.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.github.al3xcalibur.jqtool.language.psi.JqStringTokenType;
import com.github.al3xcalibur.jqtool.language.psi.JqTypes;
import com.intellij.psi.TokenType;

%%

%class JqLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=[\ \n\t\f\r]

INTEGER=-?[0-9]+
DECIMAL=-?[0-9]+\.[0-9]*

ALT="//"

STR=\"[^\"]*\"

IF="if"
THEN="then"
ELSE="else"
ELIF="elif"
END="end"

AND="and"
OR="or"
NOT="not"

TRY="try"
CATCH="catch"
LABEL="label"
BREAK="break"

AS="as"

IDENTIFIER=[a-zA-Z]+

LBRA="["
RBRA="]"
LCUR="{"
RCUR="}"
LPAR="("
RPAR=")"

INT="?"
PIPE="|"

QUOTE="\""

PLUS="+"
MINUS="-"
TIMES="*"
DIV="/"
MOD="%"

SUP=">"
INF="<"
EQU="="

AT="@"

COMMA=","
DOT="."
COLON=":"

%state WAITING_VALUE

%%
//
//<YYINITIAL> {END_OF_LINE_COMMENT}                           { yybegin(YYINITIAL); return JqTypes.COMMENT; }
//
//<YYINITIAL> {KEY_CHARACTER}+                                { yybegin(YYINITIAL); return JqTypes.KEY; }
//
//<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return JqTypes.SEPARATOR; }
//
//<WAITING_VALUE> {CRLF}({CRLF}|{WHITE_SPACE})+               { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
//
//<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }
//
//<WAITING_VALUE> {FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*   { yybegin(YYINITIAL); return JqTypes.VALUE; }

{STR}           { return JqTypes.STR; }
{INTEGER}       { return JqTypes.INTEGER; }
{DECIMAL}       { return JqTypes.DECIMAL; }
{IDENTIFIER}    { return JqTypes.IDENTIFIER; }
{PIPE}          { return JqTypes.PIPE; }
{DOT}           { return JqTypes.DOT; }
{COLON}           { return JqTypes.COLON; }
{PLUS}           { return JqTypes.PLUS; }
{MINUS}           { return JqTypes.MINUS; }
{TIMES}           { return JqTypes.TIMES; }
{DIV}           { return JqTypes.DIV; }
{MOD}           { return JqTypes.MOD; }
{LBRA}          { return JqTypes.LBRA; }
{RBRA}          { return JqTypes.RBRA; }
{LCUR}          { return JqTypes.LCUR; }
{RCUR}          { return JqTypes.RCUR; }
{LPAR}          { return JqTypes.LPAR; }
{RPAR}          { return JqTypes.RPAR; }

({WHITE_SPACE})+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }