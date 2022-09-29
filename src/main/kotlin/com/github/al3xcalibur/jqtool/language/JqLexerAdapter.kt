package com.github.al3xcalibur.jqtool.language

import com.intellij.lexer.FlexAdapter

class JqLexerAdapter : FlexAdapter(JqLexer(null))