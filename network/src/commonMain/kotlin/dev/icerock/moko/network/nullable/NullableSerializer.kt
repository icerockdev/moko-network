/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.nullable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Nullable::class)
class NullableSerializer<T : Any>(
    tSerializer: KSerializer<T>
) : KSerializer<Nullable<T>> {
    private val nullableTypeSerializer = tSerializer.nullable

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "dev.icerock.moko.network.nullable.Nullable",
        nullableTypeSerializer.descriptor
    ) { }

    override fun deserialize(decoder: Decoder): Nullable<T> {
        val value = nullableTypeSerializer.deserialize(decoder)
        return Nullable(value)
    }

    override fun serialize(encoder: Encoder, value: Nullable<T>) {
        nullableTypeSerializer.serialize(encoder, value.value)
    }
}
