package com.sagrishin.android2babel.utils

import java.io.File
import java.io.PrintStream

fun File.contains(entryName: String, fileOny: Boolean): Boolean {
    if (this.isFile) return false
    var listFiles = (listFiles { _, name -> name == entryName } ?: emptyArray()).toList()

    if (fileOny) {
        listFiles = listFiles.filter { it.isFile }
    }

    return listFiles.isNotEmpty()
}

operator fun File.contains(entryName: String): Boolean {
    return contains(entryName, true)
}

fun <T> List<T>.second(): T {
    return this[1]
}


fun <A, B> Pair<Iterable<A>, Iterable<B>>.iterator(): Iterator<Pair<A, B>> {
    val ia = first.iterator()
    val ib = second.iterator()

    return object : Iterator<Pair<A, B>> {
        override fun next(): Pair<A, B> = ia.next() to ib.next()
        override fun hasNext(): Boolean = ia.hasNext() && ib.hasNext()
    }
}


inline fun createFileAt(outputDirectory: File, fileName: String, noinline fillWith: (() -> String)? = null) {
    PrintStream(File(outputDirectory, fileName)).use { stream -> fillWith?.invoke()?.let(stream::println) }
}
