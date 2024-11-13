package com.sagrishin.android2babel.utils

import com.sagrishin.android2babel.models.TranslationRoot
import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

class AndroidTranslationsFormatterImpl : PlatformTranslationsFormatter<Document> {

    override fun format(translations: TranslationRoot): Document {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = builder.newDocument()
        val documentRoot = document.createElement("resources")

        document.appendChild(documentRoot)

        translations.strings.sortedBy { it.name }.forEach { (name, _, value) ->
            documentRoot.appendUnescaped(document.createElement("string").apply {
                setAttribute("name", name)
                textContent = value
            })
        }

        translations.arrays.sortedBy { it.name }.forEach { (name, _, items) ->
            val arrayRoot = document.createElement("string-array").apply {
                setAttribute("name", name)
            }
            documentRoot.appendChild(arrayRoot)

            items.sortedBy { it.index }.forEach {
                arrayRoot.appendUnescaped(document.createElement("item").apply {
                    textContent = it.value
                })
            }
        }

        translations.plurals.sortedBy { it.name }.forEach { (name, _, items) ->
            val pluralRoot = document.createElement("plurals").apply {
                setAttribute("name", name)
            }
            documentRoot.appendChild(pluralRoot)

            items.forEach { (quantity, value) ->
                pluralRoot.appendUnescaped(document.createElement("item").apply {
                    setAttribute("quantity", quantity)
                    textContent = value
                })
            }
        }

        return document
    }

}