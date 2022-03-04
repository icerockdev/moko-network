/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.errors

import dev.icerock.moko.resources.StringResource

data class NetworkErrorsTexts(
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
    val secureConnectionFailed: StringResource = MR.strings.secureConnectionFailedText,
    val serverCertificateHasBadDate: StringResource = MR.strings.serverCertificateHasBadDateText,
    val serverCertificateUntrusted: StringResource = MR.strings.serverCertificateUntrustedText,
    val serverCertificateHasUnknownRoot: StringResource = MR.strings.serverCertificateHasUnknownRootText,
    val serverCertificateNotYetValid: StringResource = MR.strings.serverCertificateNotYetValidText,
    val clientCertificateRejected: StringResource = MR.strings.clientCertificateRejectedText,
    val clientCertificateRequired: StringResource = MR.strings.clientCertificateRequiredText,
    val cannotLoadFromNetwork: StringResource = MR.strings.cannotLoadFromNetworkText
)
