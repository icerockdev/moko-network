/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.tests")
}

android {
    namespace = "dev.icerock.moko.network.engine"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
                api(project(":network"))
            }
        }

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
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(libs.ktorClientIos)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}
