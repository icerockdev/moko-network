/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.tasks.GenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile

class MultiPlatformNetworkGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val mokoNetworkExtension = project.extensions.create("mokoNetwork", SpecConfig::class.java)

        project.afterEvaluate {
            it.setupProject(mokoNetworkExtension)
        }
    }

    private fun Project.setupProject(mokoNetworkExtension: SpecConfig) {
        val multiplatformExtension: KotlinMultiplatformExtension = project.extensions.getByType()

        if (mokoNetworkExtension.specs.isEmpty()) return

        val openApiGenerateTask = tasks.create("openApiGenerate") {
            it.group = "moko-network"
        }

        mokoNetworkExtension.specs.forEach { spec ->
            registerSpecGenerationTask(
                spec = spec,
                openApiGenerateTask = openApiGenerateTask,
                multiplatformExtension = multiplatformExtension
            )
        }

        tasks.matching { it.name == "preBuild" }
            .all { it.dependsOn(openApiGenerateTask) }
        tasks.withType(AbstractKotlinCompile::class.java)
            .all { it.dependsOn(openApiGenerateTask) }
        tasks.withType(AbstractKotlinNativeCompile::class.java)
            .all { it.dependsOn(openApiGenerateTask) }
    }
}

private fun Project.registerSpecGenerationTask(
    spec: SpecInfo,
    openApiGenerateTask: Task,
    multiplatformExtension: KotlinMultiplatformExtension,
) {
    val generatedDir = "$buildDir/generated/moko-network/${spec.name}"
    val generatedSourcesDir = "$generatedDir/src/main/kotlin"
    val sourceSet: KotlinSourceSet? = multiplatformExtension.sourceSets.getByName(spec.sourceSet)

    sourceSet?.kotlin?.srcDir(generatedSourcesDir)

    val generateTask: TaskProvider<GenerateTask> = tasks.register(
        "${spec.name}OpenApiGenerate",
        GenerateTask::class.java
    ) { it.configure(spec, generatedDir) }

    openApiGenerateTask.dependsOn(generateTask)
}
