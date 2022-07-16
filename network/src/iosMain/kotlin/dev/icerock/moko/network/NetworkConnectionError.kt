/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.engine.ios.IosHttpRequestException

actual fun Throwable.isNetworkConnectionError(): Boolean {
    return when (this) {
        is IosHttpRequestException -> isSSLException().not()
        else -> false
    }
}
