package com.github.al3xcalibur.jqtool.language.psi

import com.github.al3xcalibur.jqtool.language.JqLanguage
import com.intellij.psi.tree.IElementType

class JqTokenType(debugName: String) : IElementType(debugName, JqLanguage) {

    fun withString(value: String): JqStringTokenType = JqStringTokenType(value, this)

    override fun toString(): String {
        return "JqTokenType." + super.toString()
    }
}

class JqStringTokenType(val value: CharSequence, val jqType: IElementType) : IElementType(jqType.debugName, JqLanguage)

fun JqTokenType.withString(value: String): JqStringTokenType = JqStringTokenType(value, this)

class JqElementType(debugName: String) : IElementType(debugName, JqLanguage)

