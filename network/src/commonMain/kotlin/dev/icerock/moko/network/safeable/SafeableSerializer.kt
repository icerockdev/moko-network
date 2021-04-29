/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.safeable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.native.concurrent.ThreadLocal

@Serializer(forClass = Safeable::class)
class SafeableSerializer<T : Any>(
    tSerializer: KSerializer<T>
) : KSerializer<Safeable<T>> {
    private val typeSerializer = tSerializer

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "dev.icerock.moko.network.safeable.Safeable",
        typeSerializer.descriptor
    ) { }

    override fun deserialize(decoder: Decoder): Safeable<T> {
        return try {
            val result = typeSerializer.deserialize(decoder)
            Safeable(result)
        } catch (cause: SerializationException) {
            val handler = deserializeExceptionHandler ?: return Safeable(null)

            if (handler(cause)) {
                Safeable(null)
            } else {
                throw cause
            }
        }
    }

    override fun serialize(encoder: Encoder, value: Safeable<T>) {
        if (value.value == null) {
            throw SerializationException("Can't encode Safeable with null value")
        }

        typeSerializer.serialize(encoder, value.value)
    }

    @ThreadLocal
    companion object {
        var deserializeExceptionHandler: ((SerializationException) -> Boolean)? = null
    }
}
