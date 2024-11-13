package com.sagrishin.android2babel.utils

fun toBabelFormat(vararg args: Any): String {
    return args.joinToString(".")
}

fun getBabelFileNameFormat(language: String): String {
    return "$language.json"
}

fun takeProjectNameFrom(babelFormatted: String): String {
    val toIndex = babelFormatted.indexOf('.').also { require(it >= 0) }
    return babelFormatted.slice(0 until toIndex)
}

fun String.getKeyWithoutProjectName(): String {
    val indexOfFirstDot = indexOfFirst { it == '.' }
    return if (indexOfFirstDot != -1) substring((indexOfFirstDot + 1)..lastIndex) else this
}
