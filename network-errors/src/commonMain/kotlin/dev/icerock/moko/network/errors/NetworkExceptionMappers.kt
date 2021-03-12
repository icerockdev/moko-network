/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.errors

import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.network.SSLExceptionType
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ValidationException
import dev.icerock.moko.network.getSSLExceptionType
import dev.icerock.moko.network.isNetworkConnectionError
import dev.icerock.moko.network.isSSLException
import dev.icerock.moko.resources.desc.CompositionStringDesc
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException

private const val INTERNAL_SERVER_ERROR_CODE_MAX = 599

/**
 * Registers all default exception mappers of the network module to [ExceptionMappersStorage].
 */
@Suppress("LongParameterList")
fun ExceptionMappersStorage.registerAllNetworkMappers(
    errorsTexts: NetworkErorrsTexts
): ExceptionMappersStorage {
    return condition<StringDesc>(
        condition = { it.isNetworkConnectionError() },
        mapper = { errorsTexts.networkConnectionErrorText.desc() }
    ).condition<StringDesc>(
        condition = { it.isSSLException() },
        mapper = {
            getSSLExceptionStringDescMapper(
                sslException = it,
                sslNetworkErrorsTexts = errorsTexts.sslNetworkErrorsTexts
            )
        }
    ).condition<StringDesc>(
        condition = { it is SerializationException },
        mapper = { errorsTexts.serializationErrorText.desc() }
    ).register<ErrorException, StringDesc> {
        getNetworkErrorExceptionStringDescMapper(
            errorException = it,
            httpNetworkErrorsTexts = errorsTexts.httpNetworkErrorsTexts
        )
    }.register<ValidationException, StringDesc>(::validationExceptionStringDescMapper)
}

/**
 * Maps the input error [exception] to default error text.
 */
private fun getNetworkErrorExceptionStringDescMapper(
    errorException: ErrorException,
    httpNetworkErrorsTexts: HttpNetworkErrorsTexts
): StringDesc {
    val httpStatusCode = errorException.httpStatusCode
    return when {
        errorException.isUnauthorized -> httpNetworkErrorsTexts.unauthorizedErrorText.desc()
        errorException.isNotFound -> httpNetworkErrorsTexts.notFoundErrorText.desc()
        errorException.isAccessDenied -> httpNetworkErrorsTexts.accessDeniedErrorText.desc()
        httpStatusCode >= HttpStatusCode.InternalServerError.value &&
                httpStatusCode <= INTERNAL_SERVER_ERROR_CODE_MAX -> {
            StringDesc.ResourceFormatted(
                httpNetworkErrorsTexts.internalServerErrorText,
                httpStatusCode
            )
        }
        else -> errorException.description?.desc() ?: ExceptionMappersStorage.getFallbackValue()
    }
}

/**
 * Maps the input error [sslException] to ssl error text.
 */
@Suppress("ComplexMethod")
private fun getSSLExceptionStringDescMapper(
    sslException: Throwable,
    sslNetworkErrorsTexts: SSLNetworkErrorsTexts
): StringDesc {
    return when (sslException.getSSLExceptionType()) {
        SSLExceptionType.SecureConnectionFailed -> sslNetworkErrorsTexts.secureConnectionFailed.desc()
        SSLExceptionType.ServerCertificateHasBadDate -> sslNetworkErrorsTexts.serverCertificateHasBadDate.desc()
        SSLExceptionType.ServerCertificateUntrusted -> sslNetworkErrorsTexts.serverCertificateUntrusted.desc()
        SSLExceptionType.ServerCertificateHasUnknownRoot -> sslNetworkErrorsTexts.serverCertificateHasUnknownRoot.desc()
        SSLExceptionType.ServerCertificateNotYetValid -> sslNetworkErrorsTexts.serverCertificateNotYetValid.desc()
        SSLExceptionType.ClientCertificateRejected -> sslNetworkErrorsTexts.clientCertificateRejected.desc()
        SSLExceptionType.ClientCertificateRequired -> sslNetworkErrorsTexts.clientCertificateRequired.desc()
        SSLExceptionType.CannotLoadFromNetwork -> sslNetworkErrorsTexts.cannotLoadFromNetwork.desc()
        else -> ExceptionMappersStorage.getFallbackValue()
    }
}

/**
 * Converts the validation [exception] to a combination of messages as one [CompositionStringDesc].
 */
private fun validationExceptionStringDescMapper(exception: ValidationException): StringDesc {
    return exception.errors.joinToString(separator = ". ") { it.message }.desc()
}
