/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Versions {
    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    const val kotlin = "1.3.61"

    private const val mokoNetwork = "0.5.1"

    object Plugins {
        const val android = "3.5.2"

        const val kotlin = Versions.kotlin
        const val mokoNetwork = Versions.mokoNetwork
    }

    object Libs {
        object Android {
            const val glide = "4.9.0"
            const val appCompat = "1.1.0"
            const val lifecycle = "2.0.0"
        }

        object MultiPlatform {
            const val serialization = "0.14.0"
            const val coroutines = "1.3.3"
            const val ktorClient = "1.2.6"
            const val mokoNetwork = Versions.mokoNetwork
            const val mokoMvvm = "0.4.0"
        }

        object Jvm {
            const val openApiGenerator = "4.2.2"
        }
    }
}