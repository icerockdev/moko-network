/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.io.File

open class SpecInfo(val name: String) {
    var inputSpec: File? = null
    var packageName: String? = "dev.icerock.moko.network.generated"
    var isInternal = true
    var isOpen = false
    internal var configureTask: (GenerateTask.() -> Unit)? = null

    fun configureTask(block: GenerateTask.() -> Unit) { configureTask = block }
}
