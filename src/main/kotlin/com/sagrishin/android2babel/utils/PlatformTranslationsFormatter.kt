package com.sagrishin.android2babel.utils

import com.sagrishin.android2babel.models.TranslationRoot

interface PlatformTranslationsFormatter<T : Any> {

    fun format(translations: TranslationRoot): T

}
