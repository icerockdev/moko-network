/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */


object Deps {
    private const val kotlinVersion = "1.4.21"

    private const val lifecycleVersion = "2.2.0"
    private const val glideVersion = "4.9.0"
    private const val androidAppCompatVersion = "1.1.0"
    private const val espressoCoreVersion = "3.2.0"
    private const val testRunnerVersion = "1.2.0"
    private const val testExtJunitVersion = "1.1.1"

    private const val openApiGeneratorVersion = "5.0.0"
    private const val kotlinxSerializationVersion = "1.0.0-RC"
    private const val coroutinesVersion = "1.4.2-native-mt"
    private const val ktorClientVersion = "1.4.0"

    private const val detektVersion = "1.15.0"

    private const val mokoGraphicsVersion = "0.5.0"
    private const val mokoParcelizeVersion = "0.5.0"
    private const val mokoResourcesVersion = "0.14.0"
    private const val mokoMvvmVersion = "0.9.1"
    private const val mokoErrorsVersion = "0.3.1"
    const val mokoNetworkVersion = "0.9.3"

    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    object Plugins {
        val javaGradle = GradlePlugin(id = "java-gradle-plugin")
        val androidApplication = GradlePlugin(id = "com.android.application")
        val androidLibrary = GradlePlugin(id = "com.android.library")
        val kotlinJvm = GradlePlugin(id = "org.jetbrains.kotlin.jvm")
        val kotlinMultiplatform = GradlePlugin(
            id = "org.jetbrains.kotlin.multiplatform",
            module = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        )
        val kotlinKapt = GradlePlugin(id = "kotlin-kapt")
        val kotlinAndroid = GradlePlugin(id = "kotlin-android")
        val kotlinAndroidExtensions = GradlePlugin(id = "kotlin-android-extensions")
        val kotlinSerialization = GradlePlugin(
            id = "org.jetbrains.kotlin.plugin.serialization",
            module = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
        )
        val mavenPublish = GradlePlugin(id = "org.gradle.maven-publish")

        val mobileMultiplatform = GradlePlugin(id = "dev.icerock.mobile.multiplatform")
        val iosFramework = GradlePlugin(id = "dev.icerock.mobile.multiplatform.ios-framework")

        val mokoNetwork = GradlePlugin(
            id = "dev.icerock.mobile.multiplatform-network-generator",
            module = "dev.icerock.moko:network-generator:$mokoNetworkVersion"
        )

        val mokoResources = GradlePlugin(
            id = "dev.icerock.mobile.multiplatform-resources",
            module = "dev.icerock.moko:resources-generator:$mokoResourcesVersion"
        )

        val detekt = GradlePlugin(
            id = "io.gitlab.arturbosch.detekt",
            version = detektVersion
        )
    }

    object Libs {
        object Android {
            const val appCompat =
                "androidx.appcompat:appcompat:$androidAppCompatVersion"
            val glide =
                "com.github.bumptech.glide:glide:$glideVersion"
            val lifecycle =
                "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"
            val ktorClientOkHttp =
                "io.ktor:ktor-client-okhttp:$ktorClientVersion"

            const val mokoMvvmDataBinding = "dev.icerock.moko:mvvm-databinding:$mokoMvvmVersion"

            object Tests {
                const val espressoCore =
                    "androidx.test.espresso:espresso-core:$espressoCoreVersion"
                const val kotlinTestJUnit =
                    "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
                const val testCore =
                    "androidx.test:core:1.3.0"
                const val robolectric =
                    "org.robolectric:robolectric:4.3"
                const val testRunner =
                    "androidx.test:runner:$testRunnerVersion"
                const val testRules =
                    "androidx.test:rules:$testRunnerVersion"
                const val testExtJunit =
                    "androidx.test.ext:junit:$testExtJunitVersion"
            }
        }

        object MultiPlatform {
            const val kotlinSerialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion"
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
            const val ktorClient =
                "io.ktor:ktor-client-core:$ktorClientVersion"
            const val ktorClientLogging =
                "io.ktor:ktor-client-logging:$ktorClientVersion"
            const val mokoResources =
                "dev.icerock.moko:resources:$mokoResourcesVersion"
            const val mokoParcelize =
                "dev.icerock.moko:parcelize:$mokoParcelizeVersion"
            const val mokoGraphics =
                "dev.icerock.moko:graphics:$mokoGraphicsVersion"
            const val mokoMvvmCore =
                "dev.icerock.moko:mvvm-core:$mokoMvvmVersion"
            const val mokoMvvmLiveData =
                "dev.icerock.moko:mvvm-livedata:$mokoMvvmVersion"
            const val mokoErrors =
                "dev.icerock.moko:errors:$mokoErrorsVersion"
            const val mokoNetwork =
                "dev.icerock.moko:network:$mokoNetworkVersion"
            const val mokoNetworkErrors =
                "dev.icerock.moko:network-errors:$mokoNetworkVersion"

            object Tests {
                const val kotlinTest =
                    "org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion"
                const val kotlinTestAnnotations =
                    "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion"
            }
        }

        object Ios {
            const val ktorClientIos =
                "io.ktor:ktor-client-ios:$ktorClientVersion"
        }

        object Jvm {
            const val openApiGenerator =
                "org.openapitools:openapi-generator-gradle-plugin:$openApiGeneratorVersion"
        }

        object Detekt {
            const val detektFormatting =
                "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion"
        }
    }
}
