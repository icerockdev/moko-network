/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */







enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("network-deps")
includeBuild("network-generator")

include(":network")
include(":network-errors")
include(":network-bignum")

include(":sample:android-app")
include(":sample:mpp-library")
