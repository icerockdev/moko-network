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
                api(libs.ktorClientOkHttp)
            }
        }

        val androidMain by getting {
            dependsOn(commonJvmAndroid)
        }

        val jvmMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                api(libs.ktorClientOkHttp)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlinTestJUnit)
            }
        }

        val iosArm64Main by getting
        val iosX64Main by getting

        iosArm64Main.dependsOn(iosX64Main)
    }
}

dependencies {

    commonMainApi(libs.kotlinSerialization)
    commonMainApi(libs.ktorClient)
    androidMainApi(libs.ktorClientOkHttp)
    iosMainApi(libs.ktorClientIos)

    androidMainImplementation(libs.appCompat)

    commonTestImplementation(libs.ktorClientMock)
    commonTestImplementation(libs.kotlinTest)
    commonTestImplementation(libs.kotlinTestAnnotations)

    androidTestImplementation(libs.kotlinTestJUnit)
}

tasks.named("publishToMavenLocal") {
    val pluginPublish = gradle.includedBuild("network-generator")
        .task(":publishToMavenLocal")
    dependsOn(pluginPublish)
}
