/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.SpecInfo.ApiVisibility
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

class MultiPlatformNetworkGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val mokoNetworkExtension = target.extensions.create("mokoNetwork", SpecConfig::class.java)

        target.afterEvaluate {

            val multiplatformExtension =
                extensions.findByType(KotlinMultiplatformExtension::class.java)
            val sourceSet =
                multiplatformExtension?.sourceSets?.getByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)

            if (mokoNetworkExtension.specs.isEmpty()) return@afterEvaluate

            val openApiGenerateTask = tasks.create("openApiGenerate") {
                group = "moko-network"
            }

            mokoNetworkExtension.specs.forEach { spec ->
                val generatedDir = "${target.buildDir}/generated/moko-network/${spec.name}"
                val generatedSourcesDir = "$generatedDir/src/main/kotlin"

                sourceSet?.kotlin?.srcDir(generatedSourcesDir)

                val generateTask: GenerateTask = tasks.create(
                    "${spec.name}OpenApiGenerate",
                    GenerateTask::class.java
                ) {
                    group = "moko-network"

                    inputSpec.set(spec.inputSpec?.path)
                    packageName.set(spec.packageName)

                    additionalProperties.set(
                        mutableMapOf(
                            "publicApi" to "${spec.apiVisibility == ApiVisibility.PUBLIC}",
                            "nonPublicApi" to "${spec.apiVisibility == ApiVisibility.INTERNAL}",
                            "openApiClasses" to "${spec.isOpen}"
                        )
                    )

                    outputDir.set(generatedDir)
                    generatorName.set("kotlin-ktor-client")
                    spec.configureTask?.invoke(this)
                }

                openApiGenerateTask.dependsOn(generateTask)

                // removing required because generate task not delete files that not exist in
                // new version of specification
                val removeGeneratedCodeTask =
                    tasks.create("${spec.name}RemoveGeneratedOpenApiCode", Delete::class.java) {
                        delete(file(generatedDir))
                    }

                generateTask.dependsOn(removeGeneratedCodeTask)
            }

            tasks.findByName("preBuild")?.dependsOn(openApiGenerateTask)
            tasks.withType<KotlinNativeCompile>().all {
                dependsOn(openApiGenerateTask)
            }
        }
    }
}
