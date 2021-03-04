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

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

kotlin {
    sourceSets {
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
    androidMainApi(Deps.Libs.Android.ktorClientOkHttp)
    iosMainApi(Deps.Libs.Ios.ktorClientIos)

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonTestImplementation(Deps.Libs.MultiPlatform.ktorClientMock)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTest)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTestAnnotations)

    androidTestImplementation(Deps.Libs.Android.Tests.kotlinTestJUnit)
}

publishing {
    repositories.maven("https://api.bintray.com/maven/icerockdev/moko/moko-network/;publish=1") {
        name = "bintray"

        credentials {
            username = System.getProperty("BINTRAY_USER")
            password = System.getProperty("BINTRAY_KEY")
        }
    }
}
