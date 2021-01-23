/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.tasks.GenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

@Suppress("ForbiddenComment")
class MultiPlatformNetworkGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val mokoNetworkExtension = target.extensions.create("mokoNetwork", SpecConfig::class.java)

        target.afterEvaluate { it.setupProject(mokoNetworkExtension) }
    }

    private fun Project.setupProject(extension: SpecConfig) {
        val multiplatformExtension =
            extensions.findByType(KotlinMultiplatformExtension::class.java)
        val sourceSet =
            multiplatformExtension?.sourceSets?.getByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)

        if (extension.specs.isEmpty()) return

        val openApiGenerateTask = tasks.create("openApiGenerate") {
            it.group = "moko-network"
        }

        extension.specs.forEach { spec ->
            val generatedDir = "$buildDir/generated/moko-network/${spec.name}"
            val generatedSourcesDir = "$generatedDir/src/main/kotlin"

            sourceSet?.kotlin?.srcDir(generatedSourcesDir)

            val generateTask: GenerateTask = tasks.create(
                "${spec.name}OpenApiGenerate",
                GenerateTask::class.java
            ) { it.configure(spec, generatedDir) }

            openApiGenerateTask.dependsOn(generateTask)

            // removing required because generate task not delete files that not exist in
            // new version of specification
            val removeGeneratedCodeTask =
                tasks.create("${spec.name}RemoveGeneratedOpenApiCode", Delete::class.java) {
                    delete(file(generatedDir))
                    it.group = "moko-network"
                }

            generateTask.dependsOn(removeGeneratedCodeTask)
        }

        tasks.findByName("preBuild")?.dependsOn(openApiGenerateTask)
        tasks.withType(KotlinNativeCompile::class.java).all { it.dependsOn(openApiGenerateTask) }
    }
}
