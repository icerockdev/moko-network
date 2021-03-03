/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.nullable

import kotlinx.serialization.Serializable

@Serializable(with = NullableSerializer::class)
data class Nullable<T : Any>(val value: T?)
