package com.sagrishin.android2babel.utils

import com.google.gson.JsonObject
import com.sagrishin.android2babel.models.ArrayTranslation
import com.sagrishin.android2babel.models.PluralTranslation
import com.sagrishin.android2babel.models.StringTranslation
import com.sagrishin.android2babel.models.TranslationRoot

class BabelParsingUseCase {

    fun parse(json: JsonObject, projectName: String): TranslationRoot {
        val strings = mutableListOf<StringTranslation>()
        val arrays = mutableMapOf<String, ArrayTranslation>()
        val plurals = mutableMapOf<String, PluralTranslation>()

        val keysPerModule = json.keySet().filter { takeProjectNameFrom(it).equals(projectName, true) }
        val valuesPerModule = keysPerModule.map { json[it].asString }
        val originalKeys = keysPerModule.map { it.getKeyWithoutProjectName() }

        for ((key, value) in (originalKeys to valuesPerModule).iterator()) {
            val keyParts = key.split('.')

            if (keyParts.size > 1) {
                when (val index = keyParts.first().toIntOrNull()) {
                    is Int -> {
                        val array = arrays.getOrPut(keyParts.first()) {
                            val count = originalKeys.count { takeProjectNameFrom(it).equals(keyParts.first(), true) }
                            ArrayTranslation(
                                name = keyParts.first(),
                                items = (0 until count).map { ArrayTranslation.ArrayItem() }.toMutableList()
                            )
                        }

                        array.items[index] = ArrayTranslation.ArrayItem(
                            index = index,
                            value = value
                        )
                    }
                    else -> {
                        val plural = plurals.getOrPut(keyParts.first()) { PluralTranslation(keyParts.first()) }
                        plural.items += PluralTranslation.PluralItem(
                            quantity = keyParts.second(),
                            value = value
                        )
                    }
                }
            } else {
                strings += StringTranslation(
                    name = key,
                    value = value
                )
            }
        }

        return TranslationRoot(
            strings = strings.sortedBy { it.name },
            arrays = arrays.values.sortedBy { it.name },
            plurals = plurals.values.sortedBy { it.name },
        )
    }

}
