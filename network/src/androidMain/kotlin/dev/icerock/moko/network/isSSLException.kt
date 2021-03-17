/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLKeyException
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLProtocolException

actual fun Throwable.isSSLException(): Boolean {
    return this is SSLException
}

actual fun Throwable.getSSLExceptionType(): SSLExceptionType? {
    return when (this) {
        is SSLHandshakeException -> SSLExceptionType.ServerCertificateUntrusted
        is SSLKeyException -> SSLExceptionType.ClientCertificateRejected
        is SSLPeerUnverifiedException -> SSLExceptionType.ClientCertificateRequired
        is SSLProtocolException -> SSLExceptionType.SecureConnectionFailed
        is SSLException -> SSLExceptionType.SecureConnectionFailed
        else -> null
    }
}
