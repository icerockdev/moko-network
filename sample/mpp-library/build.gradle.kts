/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("dev.icerock.moko.gradle.android.base")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("dev.icerock.mobile.multiplatform.targets")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.mobile.multiplatform-network-generator")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    id("dev.icerock.moko.gradle.detekt")
    id("dev.icerock.moko.gradle.tests")
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainImplementation(libs.ktorClient)
    commonMainImplementation(libs.ktorClientLogging)
    commonMainImplementation(libs.kotlinSerialization)
    commonMainImplementation(libs.ktorClientWebSocket)
    commonMainImplementation(libs.kbignum)

    commonMainApi(libs.mokoMvvmCore)
    commonMainApi(libs.mokoMvvmLiveData)
    commonMainApi(libs.mokoNetwork)
    commonMainApi(libs.mokoNetworkErrors)
    commonMainApi(libs.mokoNetworkBignum)

    commonMainApi(projects.network)
    commonMainApi(projects.networkBignum)
    commonMainApi(projects.networkErrors)

    androidMainImplementation(libs.lifecycleViewModel)
    
    commonTestImplementation(libs.ktorClientMock)
    commonTestImplementation(libs.kotlinTest)
    commonTestImplementation(libs.mokoTest)
    commonTestImplementation(libs.kotlinTestAnnotations)

    androidTestImplementation(libs.kotlinTestJUnit)
}

multiplatformResources {
    multiplatformResourcesPackage = "com.icerockdev.library"
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
