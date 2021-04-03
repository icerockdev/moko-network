/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement

class DataNotFitOneOfSchema(
    val data: JsonElement,
    val results: List<Result<*>>
) : SerializationException()
