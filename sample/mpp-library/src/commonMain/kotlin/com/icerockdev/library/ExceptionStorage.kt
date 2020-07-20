/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.lib.MR
import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.network.errors.registerAllNetworkMappers
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.serialization.SerializationException

fun initExceptionStorage() {
    ExceptionMappersStorage
        .registerAllNetworkMappers()
        .condition<StringDesc>(
            condition = { it is SerializationException },
            mapper = { MR.strings.serializationErrorText.desc() }
        )
}
