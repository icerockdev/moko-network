/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.safeable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.native.concurrent.ThreadLocal

@Serializer(forClass = Safeable::class)
class SafeableSerializer<T : Any>(
    tSerializer: KSerializer<T>
) : KSerializer<Safeable<T>> {
    private val typeSerializer = tSerializer.nullable

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "dev.icerock.moko.network.safeable.Safeable",
        typeSerializer.descriptor
    ) { }

    override fun deserialize(decoder: Decoder): Safeable<T> {
        val result = try {
            typeSerializer.deserialize(decoder)
        } catch (cause: SerializationException) {
            deserializeExceptionHandler?.let { handler ->
                if (handler(cause)) {
                    null
                } else {
                    throw cause
                }
            }
        }

        return Safeable(result)
    }

    override fun serialize(encoder: Encoder, value: Safeable<T>) {
        typeSerializer.serialize(encoder, value.value)
    }

    @ThreadLocal
    companion object {
        var deserializeExceptionHandler: ((SerializationException) -> Boolean)? = null
    }
}
