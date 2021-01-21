/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class MultiPlatformNetworkGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val mokoNetworkExtension = target.extensions.create("mokoNetwork", SpecConfig::class.java)

        target.afterEvaluate {

            val multiplatformExtension = extensions.findByType(KotlinMultiplatformExtension::class.java)
            val sourceSet = multiplatformExtension?.sourceSets?.getByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)

            mokoNetworkExtension.specs.forEach { spec ->

                val generatedDir =
                    "${target.buildDir}/generate-resources/main/src/${spec.name}"

                sourceSet?.kotlin?.srcDir(generatedDir)

                val generateTask = tasks.create(
                    "${spec.name}OpenApiGenerate",
                    org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class.java
                ) {
                    group = "moko-network"

                    inputSpec.set(spec.inputSpec?.path)
                    packageName.set(spec.packageName)

                    additionalProperties.set(
                        mutableMapOf(
                            "nonPublicApi" to "${spec.isInternal}",
                            "openApiClasses" to "${spec.isOpen}"
                        )
                    )

                    outputDir.set(generatedDir)
                    generatorName.set("kotlin-ktor-client")
                    spec.configureTask?.invoke(this)
                }

                val removeGeneratedCodeTask =
                    tasks.create("${spec.name}RemoveGeneratedOpenApiCode", Delete::class) {
                        delete(file(generatedDir))
                    }

                generateTask.dependsOn(removeGeneratedCodeTask)

                tasks.findByName("preBuild")?.dependsOn(generateTask)
                tasks.findByName("compileKotlinIosX64")?.dependsOn(generateTask)
                tasks.findByName("compileKotlinIosArm64")?.dependsOn(generateTask)
            }
        }
    }
}
