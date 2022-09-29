package com.github.al3xcalibur.jqtool.language

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon


object JqLanguage : Language("Jq")

object JqIcons {
    val FILE = IconLoader.getIcon("/icons/paste.png", JqIcons::class.java)
}

object JqFileType : LanguageFileType(JqLanguage) {

    override fun getName(): String = "Jq File"
    override fun getDescription(): String = "Jq language file"

    override fun getDefaultExtension(): String = "jq"

    override fun getIcon(): Icon = JqIcons.FILE
}