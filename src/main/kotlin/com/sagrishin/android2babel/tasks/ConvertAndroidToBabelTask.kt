package com.sagrishin.android2babel.tasks

import com.google.gson.GsonBuilder
import com.sagrishin.android2babel.models.TranslationRoot
import com.sagrishin.android2babel.utils.*
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.ConventionTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ConvertAndroidToBabelTask : ConventionTask() {

    @get:Input
    abstract val defaultLanguageName: Property<String>

    @get:Input
    abstract val outputDirectory: RegularFileProperty

    private val rootProject: Project
        get() = project.rootProject

    init {
        group = TASKS_GROUP_NAME
    }

    @TaskAction
    fun run() {
        val translationsUseCase = TranslationsRootUseCase()
        val projectTranslationsUseCase = ProjectTranslationsUseCase(defaultLanguageName.get())
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        val translationsPerSubproject = rootProject.subprojects.map(projectTranslationsUseCase::getStringsFilesOf)
        val projectLanguageNames = translationsPerSubproject.flatMap { it.translations.keys }.distinct()

        if (projectLanguageNames.isNotEmpty()) {
            val outputDirectory = outputDirectory.get().asFile
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
