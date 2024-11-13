package com.sagrishin.android2babel.tasks

import com.google.gson.GsonBuilder
import com.sagrishin.android2babel.models.TranslationRoot
import com.sagrishin.android2babel.utils.*
import org.gradle.api.Project
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ConvertAndroidToBabelTask : ConventionTask() {

    @Input
    lateinit var defaultLanguageName: String

    @Input
    lateinit var outputDirectory: File

    private val rootProject: Project
        get() = project.rootProject

    init {
        group = TASKS_GROUP_NAME
    }

    @TaskAction
    fun run() {
        val translationsUseCase = TranslationsRootUseCase()
        val projectTranslationsUseCase = ProjectTranslationsUseCase(defaultLanguageName)
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        val translationsPerSubproject = rootProject.subprojects.map(projectTranslationsUseCase::getStringsFilesOf)
        val projectLanguageNames = translationsPerSubproject.flatMap { it.translations.keys }.distinct()

        if (projectLanguageNames.isNotEmpty()) {
            outputDirectory.mkdirs()

            projectLanguageNames.forEach { language ->
                val translationRoots = translationsPerSubproject.map { (project, translations) ->
                    translations[language]
                        ?.let { translationsUseCase.getTranslationsFrom(it.file, project.name) }
                        ?: TranslationRoot()
                }

                createFileAt(outputDirectory, getBabelFileNameFormat(language)) {
                    gson.toJson(translationRoots.convertToMap())
                }
            }
        }
    }

}
