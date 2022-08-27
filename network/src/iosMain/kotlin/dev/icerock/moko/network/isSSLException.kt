/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSURLErrorCannotLoadFromNetwork
import platform.Foundation.NSURLErrorClientCertificateRequired
import platform.Foundation.NSURLErrorSecureConnectionFailed
import platform.Foundation.NSURLErrorServerCertificateHasBadDate
import platform.Foundation.NSURLErrorServerCertificateHasUnknownRoot
import platform.Foundation.NSURLErrorServerCertificateNotYetValid
import platform.Foundation.NSURLErrorServerCertificateUntrusted

private val sslKeys = mapOf(
    NSURLErrorSecureConnectionFailed to SSLExceptionType.SecureConnectionFailed,
    NSURLErrorServerCertificateHasBadDate to SSLExceptionType.ServerCertificateHasBadDate,
    NSURLErrorServerCertificateUntrusted to SSLExceptionType.ServerCertificateUntrusted,
    NSURLErrorServerCertificateHasUnknownRoot to SSLExceptionType.ServerCertificateHasUnknownRoot,
    NSURLErrorServerCertificateNotYetValid to SSLExceptionType.ServerCertificateNotYetValid,
    NSURLErrorClientCertificateRequired to SSLExceptionType.ClientCertificateRequired,
    NSURLErrorCannotLoadFromNetwork to SSLExceptionType.CannotLoadFromNetwork
)

actual fun Throwable.isSSLException(): Boolean {
    val iosHttpException = this as? DarwinHttpRequestException ?: return false
    return sslKeys.keys.contains(
        iosHttpException.origin.code
    )
}

actual fun Throwable.getSSLExceptionType(): SSLExceptionType? {
    val iosHttpException = this as? DarwinHttpRequestException ?: return null
    return sslKeys[iosHttpException.origin.code]
}
