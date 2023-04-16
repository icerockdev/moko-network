/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
}

android {
    namespace = "dev.icerock.moko.network.errors"
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
