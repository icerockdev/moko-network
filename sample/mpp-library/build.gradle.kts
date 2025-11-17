/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("dev.icerock.moko.gradle.android.base")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.mobile.multiplatform-network-generator")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.tests")
}

android {
    namespace = "com.icerockdev.library"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.ktorClient)
                implementation(libs.ktorClientLogging)
                implementation(libs.kotlinSerialization)
                implementation(libs.ktorClientWebSocket)
                implementation(libs.kbignum)

                api(libs.mokoMvvmCore)
                api(libs.mokoMvvmLiveData)

                api(project(":network"))
                api(project(":network-bignum"))
                api(project(":network-engine"))
                api(project(":network-errors"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.lifecycleViewModel)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.ktorClientMock)
                implementation(libs.mokoTest)
                implementation(libs.kotlinTestAnnotations)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlinTestJUnit)
            }
        }

        //val iosTest by creating
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

multiplatformResources {
    resourcesPackage = "com.icerockdev.library"
}

mokoNetwork {
    spec("pets") {
        inputSpec = file("src/swagger.json")
    }
    spec("profile") {
        inputSpec = file("src/profile_openapi.yaml")
        isInternal = false
        isOpen = false
    }
    spec("news") {
        inputSpec = file("wrong file")
        packageName = "news"
        isInternal = false
        isOpen = true
        configureTask {
            inputSpec.set(file("src/newsApi.yaml").path)
        }
    }
    spec("allOf") {
        packageName = "openapi.allof"
        inputSpec = file("src/allOf.yaml")
    }
    spec("anyOf") {
        packageName = "openapi.anyof"
        inputSpec = file("src/anyOf.yaml")
    }
    spec("oneOf") {
        packageName = "openapi.oneof"
        inputSpec = file("src/oneOf.yaml")
    }
    spec("mapResponse") {
        packageName = "openapi.mapResponse"
        inputSpec = file("src/mapResponse.yaml")
    }
    spec("AnyType") {
        packageName = "openapi.anyType"
        inputSpec = file("src/AnyType.yaml")
    }
    spec("formData") {
        packageName = "cases.formData"
        inputSpec = file("src/formData.yaml")
    }
    spec("enumFallbackNull") {
        packageName = "cases.enumfallback"
        enumFallbackNull = true
        inputSpec = file("src/enumFallbackNull.yaml")
    }
    spec("requestHeader") {
        packageName = "openapi.requestHeader"
        inputSpec = file("src/requestHeaders.yaml")
    }
}

val copyIosX64TestResources = tasks.register<Copy>("copyIosX64TestResources") {
    from("src/commonTest/resources")
    into("build/bin/iosX64/debugTest/resources")
}

tasks.matching { it.name == "iosX64Test" }.configureEach {
    dependsOn(copyIosX64TestResources)
}

val copyIosArm64TestResources = tasks.register<Copy>("copyIosArm64TestResources") {
    from("src/commonTest/resources")
    into("build/bin/iosSimulatorArm64/debugTest/resources")
}

tasks.matching { it.name == "iosSimulatorArm64Test" }.configureEach {
    dependsOn(copyIosArm64TestResources)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
    .matching { it.name.contains("UnitTest") }
    .configureEach {
        doLast {
            val testResourcesDir = File(projectDir, "src/commonTest/resources")
            if (testResourcesDir.exists().not()) return@doLast
            testResourcesDir.copyRecursively(destinationDirectory.get().asFile, overwrite = true)
        }
    }

