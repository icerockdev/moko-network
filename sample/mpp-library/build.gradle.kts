/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.kotlinSerialization)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mokoNetwork)
    plugin(Deps.Plugins.mokoResources)
    plugin(Deps.Plugins.iosFramework)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClient)
    commonMainImplementation(Deps.Libs.MultiPlatform.ktorClientLogging)
    commonMainImplementation(Deps.Libs.MultiPlatform.kotlinSerialization)

    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvm)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetwork)
    commonMainApi(Deps.Libs.MultiPlatform.mokoNetworkErrors)

    androidMainImplementation(Deps.Libs.Android.lifecycle)

    // temporary fix of https://youtrack.jetbrains.com/issue/KT-41821
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
}

multiplatformResources {
    multiplatformResourcesPackage = "com.icerockdev.library"
}

openApiGenerate {
    inputSpec.set(file("src/swagger.json").path)
    generatorName.set("kotlin-ktor-client")
}

openApiValidate {
    inputSpec.set(file("src/profile_openapi.yaml").path)
}
