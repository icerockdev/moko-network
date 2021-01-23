/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlin.jvm") version("1.4.21")
    id("org.gradle.maven-publish")
    id("io.gitlab.arturbosch.detekt") version("1.15.0")
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.4.21")
        classpath("gradle:network-deps:1")
    }
}

group = "dev.icerock.moko"
version = Deps.mokoNetworkVersion

dependencies {
    implementation(gradleKotlinDsl())

    implementation(Deps.Libs.Jvm.openApiGenerator)

    compileOnly(Deps.Plugins.kotlinMultiplatform.module!!)

    "detektPlugins"(Deps.Libs.Detekt.detektFormatting)
}

publishing {
    repositories.maven("https://api.bintray.com/maven/icerockdev/plugins/moko-network-generator/;publish=1") {
        name = "bintray"

        credentials {
            username = System.getProperty("BINTRAY_USER")
            password = System.getProperty("BINTRAY_KEY")
        }
    }
    publications {
        register("maven", MavenPublication::class) {
            from(components["java"])
        }
    }
}
