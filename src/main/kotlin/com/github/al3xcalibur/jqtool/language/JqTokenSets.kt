package com.github.al3xcalibur.jqtool.language

import com.github.al3xcalibur.jqtool.language.psi.JqTypes
import com.intellij.psi.tree.TokenSet

interface JqTokenSets {
    companion object {
        val IDENTIFIERS: TokenSet = TokenSet.create(JqTypes.IDENTIFIER)
    }
}