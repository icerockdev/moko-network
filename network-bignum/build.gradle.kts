/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
    id("org.gradle.maven-publish")
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)
    commonMainApi(Deps.Libs.MultiPlatform.kbignum)

    commonMainImplementation(project(":network"))
}
