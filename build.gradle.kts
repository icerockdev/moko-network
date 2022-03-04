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
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.androidGradlePlugin)
        classpath(libs.mokoGradlePlugin)
        classpath(libs.mokoResourcesGradlePlugin)
        classpath(libs.kotlinSerializationGradlePlugin)
        classpath("dev.icerock.moko:network-generator") // substituted
    }
}

apply(plugin = "dev.icerock.moko.gradle.publication.nexus")

val mokoVersion = libs.versions.mokoNetworkVersion.get()
allprojects {
    this.group = "dev.icerock.moko"
    this.version = mokoVersion

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
