/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.gradle.maven-publish")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting

        val commonJvmAndroid = create("commonJvmAndroid") {
            dependsOn(commonMain)
            dependencies {
                api(Deps.Libs.JvmAndroid.ktorClientOkHttp)
            }
        }

        val androidMain by getting {
            dependsOn(commonJvmAndroid)
        }

        val jvmMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                api(Deps.Libs.JvmAndroid.ktorClientOkHttp)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(Deps.Libs.JvmAndroid.Tests.kotlinTestJUnit)
            }
        }

        val iosArm64Main by getting
        val iosX64Main by getting

        iosArm64Main.dependsOn(iosX64Main)
    }
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines) {
        isForce = true
    }

    commonMainApi(Deps.Libs.MultiPlatform.kotlinSerialization)
    commonMainApi(Deps.Libs.MultiPlatform.ktorClient)
    androidMainApi(Deps.Libs.JvmAndroid.ktorClientOkHttp)
    iosMainApi(Deps.Libs.Ios.ktorClientIos)

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonTestImplementation(Deps.Libs.MultiPlatform.ktorClientMock)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTest)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTestAnnotations)

    androidTestImplementation(Deps.Libs.JvmAndroid.Tests.kotlinTestJUnit)
}

tasks.named("publishToMavenLocal") {
    val pluginPublish = gradle.includedBuild("network-generator")
        .task(":publishToMavenLocal")
    dependsOn(pluginPublish)
}
