/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Versions {
    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 21
    }

    const val kotlin = "1.3.50"

    private const val mokoNetwork = "0.1.0"

    object Plugins {
        const val android = "3.4.1"

        const val kotlin = Versions.kotlin
        const val androidExtensions = Versions.kotlin
        const val mokoNetwork = Versions.mokoNetwork
    }

    object Libs {
        object Android {
            const val glide = "4.9.0"
            const val appCompat = "1.0.2"
            const val lifecycle = "2.0.0"
        }

        object MultiPlatform {
            const val serialization = "0.13.0"
            const val coroutines = "1.3.0"
            const val ktorClient = "1.2.4"
            const val mokoNetwork = Versions.mokoNetwork
            const val mokoMvvm = "0.2.0"
        }

        object Jvm {
            const val openApiGenerator = "4.1.1"
        }
    }
}