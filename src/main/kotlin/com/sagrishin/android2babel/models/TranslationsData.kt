package com.sagrishin.android2babel.models

import java.io.File

data class TranslationsData constructor(
    val localeName: String,
    val file: File,
)
