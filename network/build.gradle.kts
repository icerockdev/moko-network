import org.gradle.api.internal.artifacts.dependencies.DefaultDependencyConstraint.strictly

/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("android-publication-convention")
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
    }
}

dependencies {
    commonMainImplementation(libs.coroutines) {
        version {
            require("1.5.0-native-mt")
        }
    }
    commonMainApi(libs.kotlinSerialization)
    commonMainApi(libs.ktorClient)
    androidMainApi(libs.ktorClientOkHttp)
    iosMainApi(libs.ktorClientIos)

    "androidMainImplementation"(libs.appCompat)

    commonTestImplementation(libs.ktorClientMock)
    commonTestImplementation(libs.kotlinTest)
    commonTestImplementation(libs.kotlinTestAnnotations)

    "androidTestImplementation"(libs.kotlinTestJUnit)
}

tasks.named("publishToMavenLocal") {
    val pluginPublish = gradle.includedBuild("network-generator")
        .task(":publishToMavenLocal")
    dependsOn(pluginPublish)
}
