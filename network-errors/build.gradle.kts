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
    commonMainImplementation(libs.kotlinSerialization)

    commonMainApi(libs.mokoErrors)
    commonMainApi(libs.mokoResources)

    commonMainImplementation(projects.network)
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.network.errors"
}
