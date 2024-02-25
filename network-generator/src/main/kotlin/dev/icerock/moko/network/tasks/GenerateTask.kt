/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.tasks

import dev.icerock.moko.network.KtorCodegen
import dev.icerock.moko.network.SpecInfo
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.io.File

open class GenerateTask : GenerateTask() {
    init {
        group = "moko-network"
    }

    fun configure(specInfo: SpecInfo, generatedDir: String) {
        inputSpec.set(specInfo.inputSpec?.path)
        packageName.set(specInfo.packageName)

        val excludedTags = specInfo.filterTags.joinToString(",")
        val props = mapOf(
            KtorCodegen.ADDITIONAL_OPTIONS_KEY_IS_INTERNAL to "${specInfo.isInternal}",
            KtorCodegen.ADDITIONAL_OPTIONS_KEY_IS_OPEN to "${specInfo.isOpen}",
            KtorCodegen.ADDITIONAL_OPTIONS_KEY_EXCLUDED_TAGS to excludedTags,
            KtorCodegen.ADDITIONAL_OPTIONS_KEY_ENUM_FALLBACK_NULL to "${specInfo.enumFallbackNull}"
        )
        additionalProperties.set(props)

        generatorName.set("kotlin-ktor-client")
        outputDir.set(generatedDir)

        specInfo.configureTask?.invoke(this)

        doFirst {
            // clean directory before generate new code
            File(outputDir.get()).deleteRecursively()
        }
    }
}
