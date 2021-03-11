/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.engine.ios.IosHttpRequestException
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
actual fun Throwable.isSSLException(): Boolean {
    val iosHttpException = this as? IosHttpRequestException ?: return false
    return listOf<Int>(-1200, -1201, -1202, -1203,-1204, -1205, -1206, -2000).contains(iosHttpException.origin.code.toInt())
}

@KtorExperimentalAPI
actual fun Throwable.getSSLExceptionType(): SSLExceptionType? {
    val iosHttpException = this as? IosHttpRequestException ?: return null
    return when (iosHttpException.origin.code.toInt()) {
        -1200 -> SSLExceptionType.SecureConnectionFailed
        -1201 -> SSLExceptionType.ServerCertificateHasBadDate
        -1202 -> SSLExceptionType.ServerCertificateUntrusted
        -1203 -> SSLExceptionType.ServerCertificateHasUnknownRoot
        -1204 -> SSLExceptionType.ServerCertificateNotYetValid
        -1205 -> SSLExceptionType.ClientCertificateRejected
        -1206 -> SSLExceptionType.ClientCertificateRequired
        -2000 -> SSLExceptionType.CannotLoadFromNetwork
        else -> null
    }
}
