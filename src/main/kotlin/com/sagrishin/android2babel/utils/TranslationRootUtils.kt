package com.sagrishin.android2babel.utils

import com.sagrishin.android2babel.models.TranslationRoot

fun List<TranslationRoot>.convertToMap(): Map<String, String> {
    return reduce { acc, it -> acc + it }.flattenToMap()
}

operator fun TranslationRoot.plus(another: TranslationRoot): TranslationRoot {
    return TranslationRoot(
        strings = this.strings + another.strings,
        arrays = this.arrays + another.arrays,
        plurals = this.plurals + another.plurals,
    )
}

fun TranslationRoot.flattenToMap(): Map<String, String> {
    val strings = strings.map { it.name to it.value }
    val arrays = arrays.flatMap { array -> array.items.map { toBabelFormat(array.name, it.index) to it.value } }
    val plurals = plurals.flatMap { plural -> plural.items.map { toBabelFormat(plural.name, it.quantity) to it.value } }
    return (strings + arrays + plurals).sortedBy { it.first }.toMap()
}
