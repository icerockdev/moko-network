/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
import org.gradle.api.internal.artifacts.DefaultModuleVersionSelector

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.16.2")
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
            val coroutines: MinimalExternalModuleDependency = rootProject.libs.coroutines.get()
            val forcedCoroutines: ModuleVersionSelector = DefaultModuleVersionSelector.newSelector(
                coroutines.module,
                coroutines.versionConstraint.requiredVersion
            )
            force(forcedCoroutines)
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
