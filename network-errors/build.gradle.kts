/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mokoResources)
    plugin(Deps.Plugins.mavenPublish)
}

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(Deps.Libs.MultiPlatform.mokoErrors)
    commonMainApi(Deps.Libs.MultiPlatform.mokoResources)

    commonMainImplementation(project(":network"))

    androidMainImplementation(Deps.Libs.Android.appCompat)

    // temporary fix of https://youtrack.jetbrains.com/issue/KT-41083
    commonMainImplementation("dev.icerock.moko:mvvm:0.8.0")
    commonMainImplementation("dev.icerock.moko:parcelize:0.4.0")
    commonMainImplementation("dev.icerock.moko:graphics:0.4.0")

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
