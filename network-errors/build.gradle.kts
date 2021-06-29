/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("dev.icerock.mobile.multiplatform-resources")
    id("publication-convention")
    id("javadoc-stub-convention")
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
