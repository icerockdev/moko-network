/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.utils.io.charsets.Charset
import kotlinx.serialization.toUtf8Bytes

actual fun String.toByteArrayPlatform(charset: Charset): ByteArray {
    return toUtf8Bytes()
}
