/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}

rootProject.name = "moko-network"

includeBuild("network-generator")

include(":network")
include(":network-errors")
include(":network-bignum")
include(":network-engine")

include(":sample:android-app")
include(":sample:websocket-echo-server")
include(":sample:form-data-binary-server")
include(":sample:mpp-library")
