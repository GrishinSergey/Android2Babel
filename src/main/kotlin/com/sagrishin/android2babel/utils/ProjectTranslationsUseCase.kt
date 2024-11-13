package com.sagrishin.android2babel.utils

import com.sagrishin.android2babel.models.ProjectTranslations
import com.sagrishin.android2babel.models.TranslationsData
import org.gradle.api.Project
import java.io.File
import java.nio.file.Paths

class ProjectTranslationsUseCase constructor(
    private val defaultLanguageName: String
) {

    fun getStringsFilesOf(project: Project): ProjectTranslations {
        val stringsFiles = getValuesDirectoriesWithTranslationFilesFrom(project)
        val locales = getTranslationDatasFrom(stringsFiles)
        return ProjectTranslations(project, locales.associateBy(TranslationsData::localeName))
    }

    private fun getValuesDirectoriesWithTranslationFilesFrom(project: Project): Array<File> {
        val resourcesDirectory = Paths.get(project.projectDir.absolutePath, "src", "main", "res").toFile()
        val valuesDirectories = resourcesDirectory.listFiles { dir, name ->
            name.startsWith("values") && (LOCALIZATION_FILE_NAME in File(dir, name))
        }
        return valuesDirectories ?: emptyArray()
    }

    private fun getTranslationDatasFrom(stringsFiles: Array<File>): List<TranslationsData> {
        return stringsFiles.map { valuesDirectory ->
            TranslationsData(getLocaleNameFrom(valuesDirectory), File(valuesDirectory, LOCALIZATION_FILE_NAME))
        }
    }

    private fun getLocaleNameFrom(valuesDirectory: File): String {
        return valuesDirectory.name.split("-").let { if (it.size == 1) defaultLanguageName else it.last() }
    }

}
