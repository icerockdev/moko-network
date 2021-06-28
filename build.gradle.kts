/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.16.0")
        classpath("dev.icerock.moko:network-generator") // substituted
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.20")

        classpath(":network-build-logic")
    }
}

allprojects {
    plugins.withId("org.gradle.maven-publish") {
        group = "dev.icerock.moko"
        version = libs.versions.mokoNetworkVersion.get()
    }
    configurations.configureEach {
        resolutionStrategy {
            force("${rootProject.libs.coroutines.get().module}:${rootProject.libs.coroutines.get().versionConstraint}")
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
