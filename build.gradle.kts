/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("io.gitlab.arturbosch.detekt") version("1.15.0") apply(false)
}

buildscript {
    repositories {
        mavenCentral()
        google()

        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    }
    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.15.0")
        classpath("dev.icerock.moko:network-generator") // substituted
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.4.31")
        classpath("dev.icerock.gradle:bintray-publish:0.1.0")
        classpath("gradle:network-deps:1")
    }
}

allprojects {
    apply(plugin = Deps.Plugins.detekt.id)

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        input.setFrom("src/commonMain/kotlin", "src/androidMain/kotlin", "src/iosMain/kotlin")
    }

    dependencies {
        "detektPlugins"(Deps.Libs.Detekt.detektFormatting)
    }

    plugins.withId(Deps.Plugins.androidLibrary.id) {
        configure<com.android.build.gradle.LibraryExtension> {
            compileSdkVersion(Deps.Android.compileSdk)

            defaultConfig {
                minSdkVersion(Deps.Android.minSdk)
                targetSdkVersion(Deps.Android.targetSdk)
            }
        }
    }

    plugins.withId(Deps.Plugins.mavenPublish.id) {
        group = "dev.icerock.moko"
        version = Deps.mokoNetworkVersion

        configure<PublishingExtension> {
            repositories.maven("https://api.bintray.com/maven/icerockdev/moko/moko-network/;publish=1") {
                name = "bintray"

                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_KEY")
                }
            }
        }

        apply(plugin = "dev.icerock.gradle.bintray-publish")
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
