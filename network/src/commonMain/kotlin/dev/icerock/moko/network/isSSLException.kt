/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("Filename")

package dev.icerock.moko.network

expect fun Throwable.isSSLException(): Boolean

expect fun Throwable.getSSLExceptionType(): SSLExceptionType?

enum class SSLExceptionType {
    SecureConnectionFailed,
    ServerCertificateHasBadDate,
    ServerCertificateUntrusted,
    ServerCertificateHasUnknownRoot,
    ServerCertificateNotYetValid,
    ClientCertificateRejected,
    ClientCertificateRequired,
    CannotLoadFromNetwork
}
