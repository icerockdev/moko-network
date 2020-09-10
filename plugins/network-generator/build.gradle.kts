/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    mavenLocal()

    jcenter()
    google()
}

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

dependencies {
    implementation(Deps.Libs.Jvm.openApiGenerator)

    compileOnly(Deps.Plugins.kotlinMultiplatform.module!!)
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

publishing {
    repositories.maven("https://api.bintray.com/maven/icerockdev/plugins/moko-network-generator/;publish=1") {
        name = "bintray"

        credentials {
            username = System.getProperty("BINTRAY_USER")
            password = System.getProperty("BINTRAY_KEY")
        }
    }
}
