/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.bignum

import com.soywiz.kbignum.BigNum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = BigNum::class)
object BigNumSerializer : KSerializer<BigNum> {
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "dev.icerock.moko.network.bignum.BigNumSerializer",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: BigNum) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): BigNum {
        val string = decoder.decodeString()
        return BigNum(string)
    }
}
