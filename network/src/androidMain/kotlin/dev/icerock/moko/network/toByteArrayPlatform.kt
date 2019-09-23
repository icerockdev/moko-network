/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import kotlinx.io.charsets.Charset
import kotlinx.io.core.toByteArray

actual fun String.toByteArrayPlatform(charset: Charset): ByteArray {
    return toByteArray(charset)
}