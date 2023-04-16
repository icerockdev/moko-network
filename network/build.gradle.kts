/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.tests")
}

android {
    namespace = "dev.icerock.moko.network"
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting

        val commonJvmAndroid = create("commonJvmAndroid") {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(commonJvmAndroid)
        }

        val jvmMain by getting {
            dependsOn(commonJvmAndroid)
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlinTestJUnit)
            }
        }
    }
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainApi(libs.kotlinSerialization)
    commonMainApi(libs.ktorClient)

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
