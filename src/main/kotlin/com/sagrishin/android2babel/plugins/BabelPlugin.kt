package com.sagrishin.android2babel.plugins

import com.sagrishin.android2babel.tasks.ConvertAndroidToBabelTask
import com.sagrishin.android2babel.tasks.ConvertBabelToAndroidTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

abstract class BabelPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val (defaultLanguageName, outputDirectory) = target.extensions.create(
            Android2Babel::class.java.name.decapitalize(),
            Android2Babel::class.java,
            target
        )

        target.tasks.register("android2babel", ConvertAndroidToBabelTask::class.java) {
            it.defaultLanguageName.set(defaultLanguageName)
            it.outputDirectory.set(outputDirectory)
        }

        target.tasks.register("babel2android", ConvertBabelToAndroidTask::class.java) {
            it.defaultLanguageName.set(defaultLanguageName)
            it.inputDirectory.set(outputDirectory)
        }
    }
}


abstract class Android2Babel @Inject constructor(project: Project) {
    private val objects: ObjectFactory = project.objects

    val defaultLanguageName: Property<String> = objects.property(String::class.java)
    val outputDirectory: RegularFileProperty = objects.fileProperty()

    operator fun component1(): String = defaultLanguageName.get()
    operator fun component2(): File = outputDirectory.get().asFile
}
