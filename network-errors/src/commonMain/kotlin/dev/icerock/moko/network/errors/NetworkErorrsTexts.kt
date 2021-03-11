/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.errors

import dev.icerock.moko.resources.StringResource

data class NetworkErorrsTexts(
    val networkConnectionErrorText: StringResource = MR.strings.networkConnectionErrorText,
    val serializationErrorText: StringResource = MR.strings.serializationErrorText,
    val httpNetworkErrorsTexts: HttpNetworkErrorsTexts = HttpNetworkErrorsTexts(),
    val sslNetworkErrorsTexts: SSLNetworkErrorsTexts = SSLNetworkErrorsTexts()
)

data class HttpNetworkErrorsTexts(
    val unauthorizedErrorText: StringResource = MR.strings.unauthorizedErrorText,
    val notFoundErrorText: StringResource = MR.strings.notFoundErrorText,
    val accessDeniedErrorText: StringResource = MR.strings.accessDeniedErrorText,
    val internalServerErrorText: StringResource = MR.strings.internalServerErrorText
)

data class SSLNetworkErrorsTexts(
    val default: StringResource = MR.strings.sslErrorText,
    val secureConnectionFailed: StringResource? = null,
    val serverCertificateHasBadDate: StringResource? = null,
    val serverCertificateUntrusted: StringResource? = null,
    val serverCertificateHasUnknownRoot: StringResource? = null,
    val serverCertificateNotYetValid: StringResource? = null,
    val clientCertificateRejected: StringResource? = null,
    val clientCertificateRequired: StringResource? = null,
    val cannotLoadFromNetwork: StringResource? = null
)
