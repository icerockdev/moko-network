/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
    id("org.gradle.maven-publish")
}

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)
    commonMainImplementation(Deps.Libs.MultiPlatform.kbignum)

    commonMainImplementation(project(":network"))

    androidMainImplementation(Deps.Libs.Android.appCompat)

    // temporary fix of https://youtrack.jetbrains.com/issue/KT-41821
    commonMainImplementation("io.ktor:ktor-utils:1.4.0")
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
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
