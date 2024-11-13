package com.sagrishin.android2babel.utils

import com.sagrishin.android2babel.models.ArrayTranslation
import com.sagrishin.android2babel.models.PluralTranslation
import com.sagrishin.android2babel.models.StringTranslation
import com.sagrishin.android2babel.models.TranslationRoot
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class TranslationsRootUseCase {

    fun getTranslationsFrom(stringsXml: File, projectName: String): TranslationRoot {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringsXml)

        val strings = doc.documentElement.getChildrenBy("string").map { string ->
            val attributes = string.requireAttributes()
            StringTranslation(
                name = toBabelFormat(projectName, attributes["name"]),
                isTranslatable = attributes["translatable"] ?: true,
                value = string.nodeValue()
            )
        }

        val arrays = doc.documentElement.getChildrenBy("string-array").map { array ->
            val attributes = array.requireAttributes()

            val items = array.getChildrenBy("item").mapIndexed { i, item ->
                ArrayTranslation.ArrayItem(i, item.nodeValue())
            }

            ArrayTranslation(
                name = toBabelFormat(projectName, attributes["name"]),
                isTranslatable = attributes["translatable"] ?: true,
                items = items.toMutableList()
            )
        }

        val plurals = doc.documentElement.getChildrenBy("plurals").map { plural ->
            val attributes = plural.requireAttributes()

            val items = plural.getChildrenBy("item").map {
                PluralTranslation.PluralItem(it.requireAttributes()["quantity"], it.nodeValue())
            }

            PluralTranslation(
                name = toBabelFormat(projectName, attributes["name"]),
                isTranslatable = attributes["translatable"] ?: true,
                items = items.toMutableList()
            )
        }

        return TranslationRoot(strings.toList(), arrays.toList(), plurals.toList())
    }

}
