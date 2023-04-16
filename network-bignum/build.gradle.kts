/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
}

android {
    namespace = "dev.icerock.moko.network.bignum"
}

kotlin {
    jvm()
}

dependencies {
    commonMainImplementation(libs.kotlinSerialization)
    commonMainApi(libs.kbignum)

    commonMainImplementation(projects.network)
}
