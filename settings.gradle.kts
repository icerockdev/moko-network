/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    repositories {
        mavenCentral()
        google()

        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        jcenter()

        maven { url = uri("https://kotlin.bintray.com/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://dl.bintray.com/icerockdev/moko") }

        jcenter {
            content {
                includeGroup("org.jetbrains.trove4j")
            }
        }
    }
}

includeBuild("network-deps")
includeBuild("network-generator")

include(":network")
include(":network-errors")
include(":network-bignum")

include(":sample:android-app")
include(":sample:mpp-library")
