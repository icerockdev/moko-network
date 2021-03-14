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
    id("org.jetbrains.dokka") version("1.4.20")
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClient)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClientLogging)
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmCore)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmLiveData)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetwork)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetworkErrors)

    androidMainImplementation(Deps.Libs.Android.lifecycle)

    commonTestImplementation(Deps.Libs.MultiPlatform.ktorClientMock)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTest)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.mokoTest)
    commonTestImplementation(Deps.Libs.MultiPlatform.Tests.kotlinTestAnnotations)

    androidTestImplementation(Deps.Libs.Android.Tests.kotlinTestJUnit)
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
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().all {
    dokkaSourceSets {
        named("commonMain") {
            noAndroidSdkLink.set(true)
            noJdkLink.set(true)
            reportUndocumented.set(true) 
            skipEmptyPackages.set(false)
        }
    }

    doFirst {
        dokkaSourceSets.forEach { dokkaSourceSet ->
            println("dokka source set ${dokkaSourceSet.name}")
            println("dokka source set files ${dokkaSourceSet.sourceRoots.files}")
        }
    }
}
