package com.sagrishin.android2babel.models

data class TranslationRoot constructor(
    val strings: List<StringTranslation> = mutableListOf(),
    val arrays: List<ArrayTranslation> = mutableListOf(),
    val plurals: List<PluralTranslation> = mutableListOf(),
)


data class StringTranslation constructor(
    val name: String,
    val isTranslatable: Boolean = false,
    val value: String,
)


data class ArrayTranslation constructor(
    val name: String,
    val isTranslatable: Boolean = false,
    val items: MutableList<ArrayItem> = mutableListOf(),
) {

    data class ArrayItem constructor(
        val index: Int = 0,
        val value: String = "",
    )

}


data class PluralTranslation constructor(
    val name: String,
    val isTranslatable: Boolean = false,
    val items: MutableList<PluralItem> = mutableListOf(),
) {

    data class PluralItem constructor(
        val quantity: String = "",
        val value: String = "",
    )

}
