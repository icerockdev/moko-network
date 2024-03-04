/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("Filename")

package dev.icerock.moko.network

import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

actual fun Throwable.isNetworkConnectionError(): Boolean {
    return when (this) {
        is ConnectException,
        is SocketException,
        is SocketTimeoutException,
        is TimeoutException,
        is UnknownHostException -> true
        else -> false
    }
}
