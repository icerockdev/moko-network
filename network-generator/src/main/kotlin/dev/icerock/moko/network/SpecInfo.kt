/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.io.File

open class SpecInfo(val name: String) {
    var inputSpec: File? = null
    var sourceSet: String = KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME
    var packageName: String? = "dev.icerock.moko.network.generated"
    var isInternal = true
    var isOpen = false
    var filterTags: List<String> = listOf()
    internal var configureTask: (GenerateTask.() -> Unit)? = null

    fun configureTask(block: GenerateTask.() -> Unit) { configureTask = block }
}
