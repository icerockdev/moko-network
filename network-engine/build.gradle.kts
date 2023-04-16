/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.tests")
}

android {
    namespace = "dev.icerock.moko.network.engine"
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
        }
    }
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainApi(projects.network)
    iosMainApi(libs.ktorClientIos)
}
