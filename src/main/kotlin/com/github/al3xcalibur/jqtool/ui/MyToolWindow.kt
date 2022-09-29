package com.github.al3xcalibur.jqtool.ui

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.al3xcalibur.jqtool.language.JqLanguage
import com.google.gson.GsonBuilder
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.HighlightDisplayKey
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInspection.InspectionProfile
import com.intellij.codeInspection.ex.InspectionProfileImpl
import com.intellij.codeInspection.ex.InspectionProfileWrapper
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper
import com.intellij.json.JsonLanguage
import com.intellij.json.codeinsight.JsonStandardComplianceInspection
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.ErrorStripeEditorCustomization
import com.intellij.ui.LanguageTextField
import com.intellij.ui.SimpleEditorCustomization
import com.intellij.util.containers.ContainerUtil
import net.thisptr.jackson.jq.JsonQuery
import net.thisptr.jackson.jq.Scope
import net.thisptr.jackson.jq.Version
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.util.*
import java.util.function.Function
import javax.swing.*

class MyToolWindow(val toolWindow: ToolWindow, private val myProject: Project) {

    lateinit var myToolContent: JPanel
    lateinit var txtJson: LanguageTextField
    lateinit var txtResult: LanguageTextField
    lateinit var btPaste: JButton
    lateinit var btFormat: JButton
    lateinit var btCopy: JButton
    lateinit var label: JLabel
    lateinit var lbResult: JLabel
    lateinit var txtPath: LanguageTextField

    private var mJson: String = ""
    private var mJsonPath: String = ""

    val mapper = ObjectMapper()
    val gson = GsonBuilder().setPrettyPrinting().create()

    val scope = Scope.newEmptyScope()

    private val logger: Logger = Logger.getInstance(MyToolWindow::class.java)

    init {
        mapper.setDefaultPrettyPrinter(MyPrettyPrinter())

        btFormat.addActionListener { _: ActionEvent? ->
            updateJsonTexts()
            label.text = ""
            try {
                txtJson.text = getPrettyJson(mJson)
            } catch (e: Exception) {
                label.text = " Invalid JSON"
            }
        }

        btCopy.addActionListener { _: ActionEvent? ->
            updateJsonTexts()
            try {
                Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(mJsonPath), null)
            } catch (e: Exception) {
                label.text = " Error copying"
            }
        }

        btPaste.addActionListener { _: ActionEvent? ->
            try {
                txtJson.setText(
                    getJqJson(
                        Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor)
                            .toString(),
                        mJsonPath
                    )
                )
            } catch (e1: Exception) {
                txtJson.text = ""
            }
            evaluate()
        }

        txtJson.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                evaluate()
            }
        })

        txtPath.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                evaluate()
            }
        })
    }

    private fun getPrettyJson(json: String): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(json))
    }

    private fun getJqJson(json: String, jsonPath: String): String? {
        return try {
            val q = JsonQuery.compile(jsonPath, Version.LATEST)
            val x = mapper.readTree(json)
            val out = mutableListOf<JsonNode>()
            q.apply(scope, x, out::add)
            out.joinToString("\n") {
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it)
            }
        } catch (e: java.lang.Exception) {
            "No match"
        }
    }

    /**
     * evaluate JSONPath expression and update jTextArea with result
     */
    private fun evaluate() {
        updateJsonTexts()
        txtResult.setText(getJqJson(mJson, mJsonPath))
    }

    /**
     * update JSONPath and JSON Strings with related jTextFields
     */
    private fun updateJsonTexts() {
        mJson = txtJson.text
        mJsonPath = txtPath.text
    }

    private fun createUIComponents() {
        txtJson = JsonEditorField(myProject, "", true, true)
        txtResult = JsonEditorField(myProject, "", false, false)
        txtPath = JqEditorField(myProject, "")
    }

    fun getContent(): JComponent {
        return myToolContent
    }
}

class JsonEditorField(project: Project, s: String, hasErrorStripe: Boolean, useSpellCheck: Boolean) :
    CustomEditorField(JsonLanguage.INSTANCE, project, s, hasErrorStripe, useSpellCheck, false)

class JqEditorField(project: Project, s: String) : CustomEditorField(JqLanguage, project, s, true, true, true)

open class CustomEditorField(
    language: Language,
    project: Project,
    s: String,
    val hasErrorStripe: Boolean,
    val useSpellCheck: Boolean,
    val isOneLine: Boolean
) : LanguageTextField(language, project, s) {

    override fun createEditor(): EditorEx {
        val editor = super.createEditor()
        editor.apply {
            setVerticalScrollbarVisible(true)
            setHorizontalScrollbarVisible(true)
            isOneLineMode = isOneLine
        }

        val errorStripe =
            if (hasErrorStripe) ErrorStripeEditorCustomization.ENABLED else ErrorStripeEditorCustomization.DISABLED
        errorStripe.customize(editor)

        SyntaxCheckingEditorCustomization(useSpellCheck).customize(editor)

        editor.settings.apply {
            isLineNumbersShown = !isOneLine
            isAutoCodeFoldingEnabled = true
            isFoldingOutlineShown = true
            isAllowSingleLogicalLineFolding = true
            isRightMarginShown = true
        }
        return editor
    }
}

@Suppress("UnstableApiUsage")
class SyntaxCheckingEditorCustomization internal constructor(enabled: Boolean) : SimpleEditorCustomization(enabled) {
    override fun customize(editor: EditorEx) {
        val apply = isEnabled
        if (!READY) {
            return
        }
        val project = editor.project ?: return
        val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return
        var strategy = file.getUserData(InspectionProfileWrapper.CUSTOMIZATION_KEY)
        if (strategy == null) {
            file.putUserData(InspectionProfileWrapper.CUSTOMIZATION_KEY, MyInspectionProfileStrategy().also {
                strategy = it
            })
        }
        if (strategy !is MyInspectionProfileStrategy) {
            return
        }
        (strategy as MyInspectionProfileStrategy).setUseSpellCheck(apply)
        if (apply) {
            editor.putUserData(IntentionManager.SHOW_INTENTION_OPTIONS_KEY, false)
        }

        // Update representation.
        val analyzer = DaemonCodeAnalyzer.getInstance(project)
        analyzer?.restart(file)
    }

    private class MyInspectionProfileStrategy :
        Function<InspectionProfileImpl, InspectionProfileWrapper> {
        private val myWrappers: MutableMap<InspectionProfile, MyInspectionProfileWrapper> =
            ContainerUtil.createWeakMap()
        var myUseSpellCheck = false
        override fun apply(inspectionProfile: InspectionProfileImpl): InspectionProfileWrapper {
            if (!READY) {
                return InspectionProfileWrapper(inspectionProfile)
            }
            var wrapper = myWrappers[inspectionProfile]
            if (wrapper == null) {
                myWrappers[inspectionProfile] = MyInspectionProfileWrapper(inspectionProfile).also { wrapper = it }
            }
            wrapper!!.setUseSpellCheck(myUseSpellCheck)
            return wrapper!!
        }

        fun setUseSpellCheck(useSpellCheck: Boolean) {
            myUseSpellCheck = useSpellCheck
        }
    }

    private class MyInspectionProfileWrapper(inspectionProfile: InspectionProfileImpl) :
        InspectionProfileWrapper(inspectionProfile) {
        private var myUseSpellCheck = false
        override fun isToolEnabled(key: HighlightDisplayKey, element: PsiElement): Boolean {
            return if (SPELL_CHECK_TOOLS.containsKey(key.toString())) myUseSpellCheck else super.isToolEnabled(
                key,
                element
            )
        }

        fun setUseSpellCheck(useSpellCheck: Boolean) {
            myUseSpellCheck = useSpellCheck
        }
    }

    companion object {
        private val SPELL_CHECK_TOOLS: MutableMap<String, LocalInspectionToolWrapper> = HashMap()
        private val READY: Boolean = init()

        private fun init(): Boolean {
            // It's assumed that default spell checking inspection settings are just fine for processing all types of data.
            // Please perform corresponding settings tuning if that assumption is broken in the future.
            val inspectionClasses = arrayOf(JsonStandardComplianceInspection::class.java)
            for (inspectionClass in inspectionClasses) {
                try {
                    val tool = inspectionClass.getDeclaredConstructor().newInstance()
                    SPELL_CHECK_TOOLS[tool.shortName] = LocalInspectionToolWrapper(tool)
                } catch (e: Throwable) {
                    return false
                }
            }
            return true
        }

        fun isSpellCheckingDisabled(file: PsiFile): Boolean {
            val strategy = file.getUserData(InspectionProfileWrapper.CUSTOMIZATION_KEY)!!
            return strategy is MyInspectionProfileStrategy && !strategy.myUseSpellCheck
        }

        val spellCheckingToolNames: Set<String>
            get() = Collections.unmodifiableSet(SPELL_CHECK_TOOLS.keys)
    }
}
