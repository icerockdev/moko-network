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

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(Deps.Libs.MultiPlatform.mokoErrors)
    commonMainApi(Deps.Libs.MultiPlatform.mokoResources)

    commonMainImplementation(project(":network"))

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonMainImplementation(Deps.Libs.MultiPlatform.mokoMvvmCore)
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoParcelize)
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoGraphics)
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.network.errors"
}
