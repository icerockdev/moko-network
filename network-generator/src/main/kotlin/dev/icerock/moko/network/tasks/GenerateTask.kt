package dev.icerock.moko.network.tasks

import dev.icerock.moko.network.KtorCodegen
import dev.icerock.moko.network.SpecInfo
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

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
            KtorCodegen.ADDITIONAL_OPTIONS_KEY_EXCLUDED_TAGS to excludedTags
        )
        additionalProperties.set(props)

        generatorName.set("kotlin-ktor-client")
        outputDir.set(generatedDir)

        specInfo.configureTask?.invoke(this)
    }
}
