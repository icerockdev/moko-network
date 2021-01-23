/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
    id("dev.icerock.mobile.multiplatform-resources")
    id("org.gradle.maven-publish")
}

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(Deps.Libs.MultiPlatform.mokoErrors)
    commonMainApi(Deps.Libs.MultiPlatform.mokoResources)

    commonMainImplementation(project(":network"))

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonMainImplementation(Deps.Libs.MultiPlatform.mokoMvvmCore)
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoParcelize)
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoGraphics)

    // temporary fix of https://youtrack.jetbrains.com/issue/KT-41821
    commonMainImplementation("io.ktor:ktor-utils:1.4.0")
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.network.errors"
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
