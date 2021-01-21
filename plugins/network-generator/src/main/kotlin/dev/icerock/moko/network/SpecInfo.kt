/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.io.File

open class SpecInfo(val name: String) {
    var inputSpec: File? = null
    var packageName: String? = "dev.icerock.moko.network.generated"
    var apiVisibility = ApiVisibility.INTERNAL
    var isOpen = false

    @Deprecated("Use more generic `apiVisibility` attribute", ReplaceWith("apiVisibility"))
    var isInternal
        get() = apiVisibility == ApiVisibility.INTERNAL
        set(value) {
            apiVisibility = if (value) ApiVisibility.INTERNAL else ApiVisibility.DEFAULT
        }

    internal var configureTask: (GenerateTask.() -> Unit)? = null

    fun configureTask(block: GenerateTask.() -> Unit) { configureTask = block }

    enum class ApiVisibility {
        /** Generated classes have `public` visibility modifier. Useful for Kotlin Explicit API mode. */
        PUBLIC,

        /** Generated classes have no visibility modifier. */
        DEFAULT,

        /** Generated classes have `internal` visibility modifier. */
        INTERNAL,
    }
}
