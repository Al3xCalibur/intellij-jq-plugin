package com.github.al3xcalibur.jqtool.language

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class JqColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon {
        return JqIcons.FILE
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return JqSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return """
            # You are reading the ".properties" entry.
            ! The exclamation mark can also mark text as comments.
            website = https://en.wikipedia.org/
            language = English
            # The backslash below tells the application to continue reading
            # the value onto the next line.
            message = Welcome to \
                      Wikipedia!
            # Add spaces to the key
            key\ with\ spaces = This is the value that could be looked up with the key "key with spaces".
            # Unicode
            tab : \u0009""".trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        return mapOf()
    }

    override fun getAttributeDescriptors(): Array<out AttributesDescriptor> {
        return DESCRIPTORS
    }

    override fun getColorDescriptors(): Array<out ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "Jq"
    }

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Key", JqSyntaxHighlighter.KEY),
            AttributesDescriptor("Separator", JqSyntaxHighlighter.SEPARATOR),
            AttributesDescriptor("Value", JqSyntaxHighlighter.VALUE),
            AttributesDescriptor("Bad value", JqSyntaxHighlighter.BAD_CHARACTER)
        )
    }
}