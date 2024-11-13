package com.sagrishin.android2babel.models

import org.gradle.api.Project

data class ProjectTranslations constructor(
    val project: Project,
    val translations: Map<String, TranslationsData>,
)
