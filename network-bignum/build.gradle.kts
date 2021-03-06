/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("publication-convention")
    id("javadoc-stub-convention")
}

dependencies {
    commonMainImplementation(libs.kotlinSerialization)
    commonMainApi(libs.kbignum)

    commonMainImplementation(projects.network)
}
