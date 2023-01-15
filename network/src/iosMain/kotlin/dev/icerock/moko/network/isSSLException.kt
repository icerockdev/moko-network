/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import platform.Foundation.NSError
import platform.Foundation.NSURLErrorCannotLoadFromNetwork
import platform.Foundation.NSURLErrorClientCertificateRequired
import platform.Foundation.NSURLErrorDomain
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
    val nsError: NSError = ThrowableToNSErrorMapper(this) ?: return false

    return nsError.domain == NSURLErrorDomain && sslKeys.keys.contains(nsError.code)
}

actual fun Throwable.getSSLExceptionType(): SSLExceptionType? {
    val nsError: NSError = ThrowableToNSErrorMapper(this) ?: return null
    if (nsError.domain != NSURLErrorDomain) return null

    return sslKeys[nsError.code]
}
