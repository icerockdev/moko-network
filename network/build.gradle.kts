/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.tests")
}

android {
    namespace = "dev.icerock.moko.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
                api(libs.kotlinSerialization)
                api(libs.ktorClient)
            }
        }

        val commonJvmAndroid by creating {
            dependsOn(commonMain)
        }
        val androidMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                implementation(libs.appCompat)
            }
        }
        val jvmMain by getting {
            dependsOn(commonJvmAndroid)
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.ktorClientMock)
                implementation(libs.kotlinTestAnnotations)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlinTestJUnit)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

tasks.named("publishToMavenLocal") {
    val pluginPublish = gradle.includedBuild("network-generator")
        .task(":publishToMavenLocal")
    dependsOn(pluginPublish)
}
