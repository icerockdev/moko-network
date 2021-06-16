/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("dev.icerock.mobile.multiplatform")
    id("dev.icerock.mobile.multiplatform-resources")
    id("dev.icerock.mobile.multiplatform-network-generator")
    id("dev.icerock.mobile.multiplatform.ios-framework")
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClient)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClientLogging)
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)
    commonMainImplementation(Deps.Libs.MultiPlatform.kbignum)

    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmCore)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmLiveData)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetwork)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetworkErrors)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetworkBignum)

    androidMainImplementation(Deps.Libs.Android.lifecycle)

    commonTestImplementation(Deps.Libs.MultiPlatform.ktorClientMock)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTest)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.mokoTest)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTestAnnotations)

    androidTestImplementation(Deps.Libs.JvmAndroid.Tests.kotlinTestJUnit)
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
}
