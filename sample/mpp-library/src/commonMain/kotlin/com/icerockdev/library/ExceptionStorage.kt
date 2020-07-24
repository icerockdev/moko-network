/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.network.errors.registerAllNetworkMappers

fun initExceptionStorage() {
    ExceptionMappersStorage.registerAllNetworkMappers()
}
