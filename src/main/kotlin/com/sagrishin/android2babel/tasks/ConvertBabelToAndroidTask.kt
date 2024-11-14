package com.sagrishin.android2babel.tasks

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sagrishin.android2babel.models.TranslationRoot
import com.sagrishin.android2babel.utils.AndroidTranslationsFormatterImpl
import com.sagrishin.android2babel.utils.BabelParsingUseCase
import com.sagrishin.android2babel.utils.TASKS_GROUP_NAME
import com.sagrishin.android2babel.utils.takeProjectNameFrom
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Paths
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

abstract class ConvertBabelToAndroidTask : DefaultTask()  {

    @get:Input
    abstract val defaultLanguageName: Property<String>

    @get:Input
    abstract val inputDirectory: RegularFileProperty

    private val rootProject: Project
        get() = project.rootProject

    private val formatter = AndroidTranslationsFormatterImpl()

    init {
        group = TASKS_GROUP_NAME
    }

    @TaskAction
    fun run() {
        val gson = Gson()
        val babelParsingUseCase = BabelParsingUseCase()

        (inputDirectory.get().asFile.listFiles() ?: emptyArray()).map { file ->
            /// Iterating per language
            val babelFormattedTranslations = gson.fromJson(file.reader(), JsonObject::class.java)
            val moduleNames = babelFormattedTranslations.keySet().map(::takeProjectNameFrom).distinct()

            rootProject.subprojects.asSequence().filter { it.name in moduleNames }.forEach { project ->
                /// Iterating subprojects of root project
                val translations = babelParsingUseCase.parse(babelFormattedTranslations, project.name)
                formatTranslationsToFile(translations, file.nameWithoutExtension)
            }
        }
    }

    private fun formatTranslationsToFile(root: TranslationRoot, translationsLanguage: String) {
        val stringsFile = getAccordingValuesFolder(project, translationsLanguage)
        TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")
            transform(DOMSource(formatter.format(root)), StreamResult(stringsFile))
        }
    }

    private fun getAccordingValuesFolder(project: Project, translationsLanguage: String): File {
        val defaultLanguageName = defaultLanguageName.get()
        val valuesFolder = when (translationsLanguage) {
            defaultLanguageName -> "values"
            else -> "values-$translationsLanguage"
        }
        val resDirectory = Paths.get(project.projectDir.absolutePath, "src", "main", "res", valuesFolder).toFile()
        return File(resDirectory, "strings.xml").apply {
            parentFile.mkdirs()
            createNewFile()
        }
    }

}
