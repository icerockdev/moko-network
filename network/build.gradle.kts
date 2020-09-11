/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
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
