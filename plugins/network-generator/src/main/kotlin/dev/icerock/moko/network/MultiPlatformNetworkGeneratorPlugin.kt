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
import org.openapitools.generator.gradle.plugin.OpenApiGeneratorPlugin

class MultiPlatformNetworkGeneratorPlugin : Plugin<Project> {
    private val openApiGenerator = OpenApiGeneratorPlugin()

    override fun apply(target: Project) {

        val mokoNetworkExtension = target.extensions.create("mokoNetwork", SpecConfig::class.java)

        target.afterEvaluate {

            mokoNetworkExtension.specs.forEach { spec ->

                val generatedDir = "${target.buildDir}/generate-resources/main/src/main/kotlin/${spec.name}"

                extensions.findByType(KotlinMultiplatformExtension::class.java)?.run {
                    val sourceSet = sourceSets.getByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
                    val sources = generatedDir
                    sourceSet.kotlin.srcDir(sources)
                }

                val generateTask =tasks.create("${spec.name}OpenApiGenerate", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class.java) {
                    group = "openapi"

                    inputSpec.set(spec.inputSpec?.path)
                    packageName.set(spec.packageName)

                    outputDir.set(generatedDir)
                    generatorName.set("kotlin-ktor-client")
                    spec.configureTask?.invoke(this)
                }

                val removeGeneratedCodeTask =
                    tasks.create("${spec.name}removeGeneratedOpenApiCode", Delete::class) {
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
