package com.github.al3xcalibur.jqtool.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class JqFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JqLanguage) {

    override fun toString(): String {
        return "Jq File"
    }

    override fun getFileType(): FileType {
        return JqFileType
    }
}