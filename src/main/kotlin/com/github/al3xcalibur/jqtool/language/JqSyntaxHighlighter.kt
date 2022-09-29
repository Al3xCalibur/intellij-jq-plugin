package com.github.al3xcalibur.jqtool.language

import com.github.al3xcalibur.jqtool.language.psi.JqTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType


class JqSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return JqLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            JqTypes.STR -> VALUE_KEYS

            JqTypes.INTEGER, JqTypes.DECIMAL -> NUMBER_KEYS

            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS

            else -> EMPTY_KEYS
        }
    }

    companion object {
        val SEPARATOR: TextAttributesKey =
            createTextAttributesKey("JQ_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val KEY: TextAttributesKey = createTextAttributesKey("JQ_KEY", DefaultLanguageHighlighterColors.KEYWORD)
        val VALUE: TextAttributesKey = createTextAttributesKey("JQ_VALUE", DefaultLanguageHighlighterColors.STRING)
        val NUMBER: TextAttributesKey = createTextAttributesKey("JQ_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val COMMENT: TextAttributesKey =
            createTextAttributesKey("JQ_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER: TextAttributesKey =
            createTextAttributesKey("JQ_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BAD_CHAR_KEYS: Array<TextAttributesKey> = arrayOf(BAD_CHARACTER)
        private val SEPARATOR_KEYS: Array<TextAttributesKey> = arrayOf(SEPARATOR)
        private val KEY_KEYS: Array<TextAttributesKey> = arrayOf(KEY)
        private val VALUE_KEYS: Array<TextAttributesKey> = arrayOf(VALUE)
        private val NUMBER_KEYS: Array<TextAttributesKey> = arrayOf(NUMBER)
        private val COMMENT_KEYS: Array<TextAttributesKey> = arrayOf(COMMENT)
        private val EMPTY_KEYS: Array<TextAttributesKey> = arrayOf()
    }
}