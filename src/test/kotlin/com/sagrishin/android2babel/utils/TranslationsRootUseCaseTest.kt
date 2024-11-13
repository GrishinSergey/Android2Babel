package com.sagrishin.android2babel.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class TranslationsRootUseCaseTest {

    @Test
    fun getTranslationsFrom() {
        val file = File(requireNotNull(javaClass.classLoader.getResource("test-strings-en.xml")).file)
        val useCase = TranslationsRootUseCase()
        val gson = GsonBuilder().disableHtmlEscaping().create()

        val projectName = "test"
        val translations = gson.toJsonTree(useCase.getTranslationsFrom(file, projectName).flattenToMap())
        val expectedText = requireNotNull(javaClass.classLoader.getResource("test-strings-en.json")).readText()
        val expected = gson.fromJson(expectedText, JsonObject::class.java)

        Assertions.assertEquals(expected, translations)
    }

}
