/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import platform.Foundation.NSError
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorCannotLoadFromNetwork
import platform.Foundation.NSURLErrorDomain

actual fun Throwable.isNetworkConnectionError(): Boolean {
    val nsError: NSError? = ThrowableToNSErrorMapper(this)

    return when {
        this.isSSLException().not() -> true

        nsError?.domain == NSURLErrorDomain && nsError?.code in listOf(
            NSURLErrorCannotConnectToHost,
            NSURLErrorCannotFindHost,
            NSURLErrorCannotLoadFromNetwork
        ) -> true

        else -> false
    }
}
