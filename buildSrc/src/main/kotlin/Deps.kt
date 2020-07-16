/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Deps {
    object Plugins {
        const val android =
            "com.android.tools.build:gradle:${Versions.Plugins.android}"
        const val kotlin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlin}"
        const val kotlinSerialization =
            "org.jetbrains.kotlin:kotlin-serialization:${Versions.Plugins.kotlin}"
        const val mokoNetwork =
            "dev.icerock.moko:network-generator:${Versions.Plugins.mokoNetwork}"
        const val mokoResources =
            "dev.icerock.moko:resources-generator:${Versions.Plugins.mokoResources}"
    }

    object Libs {
        object Android {
            val glide = AndroidLibrary(
                name = "com.github.bumptech.glide:glide:${Versions.Libs.Android.glide}"
            )
            val kotlinStdLib = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
            )
            val appCompat = AndroidLibrary(
                name = "androidx.appcompat:appcompat:${Versions.Libs.Android.appCompat}"
            )
            val lifecycle = AndroidLibrary(
                name = "androidx.lifecycle:lifecycle-extensions:${Versions.Libs.Android.lifecycle}"
            )
        }

        object MultiPlatform {
            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
            )
            val serialization = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.Libs.MultiPlatform.serialization}",
                common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.Libs.MultiPlatform.serialization}",
                ios = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.Libs.MultiPlatform.serialization}"
            )
            val coroutines = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.MultiPlatform.coroutines}",
                common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.Libs.MultiPlatform.coroutines}",
                ios = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.Libs.MultiPlatform.coroutines}"
            )
            val ktorClient = MultiPlatformLibrary(
                android = "io.ktor:ktor-client-android:${Versions.Libs.MultiPlatform.ktorClient}",
                common = "io.ktor:ktor-client-core:${Versions.Libs.MultiPlatform.ktorClient}",
                ios = "io.ktor:ktor-client-ios:${Versions.Libs.MultiPlatform.ktorClient}"
            )
            val ktorClientLogging = MultiPlatformLibrary(
                android = "io.ktor:ktor-client-logging-jvm:${Versions.Libs.MultiPlatform.ktorClientLogging}",
                common = "io.ktor:ktor-client-logging:${Versions.Libs.MultiPlatform.ktorClientLogging}",
                ios = "io.ktor:ktor-client-logging-native:${Versions.Libs.MultiPlatform.ktorClientLogging}"
            )
            val mokoNetwork = MultiPlatformLibrary(
                common = "dev.icerock.moko:network:${Versions.Libs.MultiPlatform.mokoNetwork}",
                iosX64 = "dev.icerock.moko:network-iosx64:${Versions.Libs.MultiPlatform.mokoNetwork}",
                iosArm64 = "dev.icerock.moko:network-iosarm64:${Versions.Libs.MultiPlatform.mokoNetwork}"
            )
            val mokoNetworkErrors = MultiPlatformLibrary(
                common = "dev.icerock.moko:network-errors:${Versions.Libs.MultiPlatform.mokoNetwork}",
                iosX64 = "dev.icerock.moko:network-errors-iosx64:${Versions.Libs.MultiPlatform.mokoNetwork}",
                iosArm64 = "dev.icerock.moko:network-errors-iosarm64:${Versions.Libs.MultiPlatform.mokoNetwork}"
            )
            val mokoMvvm = MultiPlatformLibrary(
                common = "dev.icerock.moko:mvvm:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosX64 = "dev.icerock.moko:mvvm-iosx64:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosArm64 = "dev.icerock.moko:mvvm-iosarm64:${Versions.Libs.MultiPlatform.mokoMvvm}"
            )
            val mokoResources = MultiPlatformLibrary(
                common = "dev.icerock.moko:resources:${Versions.Libs.MultiPlatform.mokoResources}",
                iosX64 = "dev.icerock.moko:resources-iosx64:${Versions.Libs.MultiPlatform.mokoResources}",
                iosArm64 = "dev.icerock.moko:resources-iosarm64:${Versions.Libs.MultiPlatform.mokoResources}"
            )
            val mokoErrors = MultiPlatformLibrary(
                common = "dev.icerock.moko:errors:${Versions.Libs.MultiPlatform.mokoErrors}",
                iosX64 = "dev.icerock.moko:errors-iosx64:${Versions.Libs.MultiPlatform.mokoErrors}",
                iosArm64 = "dev.icerock.moko:errors-iosarm64:${Versions.Libs.MultiPlatform.mokoErrors}"
            )
        }

        object Jvm {
            const val openApiGenerator =
                "org.openapitools:openapi-generator-gradle-plugin:${Versions.Libs.Jvm.openApiGenerator}"
        }
    }

    val plugins: Map<String, String> = mapOf(
        "com.android.application" to Plugins.android,
        "com.android.library" to Plugins.android,
        "org.jetbrains.kotlin.multiplatform" to Plugins.kotlin,
        "dev.icerock.mobile.multiplatform-resources" to Plugins.mokoResources,
        "kotlin-kapt" to Plugins.kotlin,
        "kotlin-android" to Plugins.kotlin,
        "kotlinx-serialization" to Plugins.kotlinSerialization,
        "dev.icerock.mobile.multiplatform-network-generator" to Plugins.mokoNetwork
    )
}