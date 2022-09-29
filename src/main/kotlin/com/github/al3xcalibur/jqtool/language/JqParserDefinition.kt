package com.github.al3xcalibur.jqtool.language

import com.github.al3xcalibur.jqtool.language.psi.JqTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class JqParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return JqLexerAdapter()
    }

    override fun createParser(project: Project): PsiParser {
        return JqParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(JqTypes.STR)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return JqFile(viewProvider)
    }

    override fun createElement(node: ASTNode): PsiElement {
        return JqTypes.Factory.createElement(node)
    }

    companion object {
        val FILE: IFileElementType = IFileElementType(JqLanguage)
    }
}