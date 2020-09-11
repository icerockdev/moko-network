/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.errors

import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ValidationException
import dev.icerock.moko.network.isNetworkConnectionError
import dev.icerock.moko.resources.StringResource
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
    networkConnectionErrorText: StringResource = MR.strings.networkConnectionErrorText,
    unauthorizedErrorText: StringResource = MR.strings.unauthorizedErrorText,
    notFoundErrorText: StringResource = MR.strings.notFoundErrorText,
    accessDeniedErrorText: StringResource = MR.strings.accessDeniedErrorText,
    internalServerErrorText: StringResource = MR.strings.internalServerErrorText,
    serializationErrorText: StringResource = MR.strings.serializationErrorText
): ExceptionMappersStorage {
    return condition<StringDesc>(
        condition = { it.isNetworkConnectionError() },
        mapper = { networkConnectionErrorText.desc() }
    ).condition<StringDesc>(
        condition = { it is SerializationException },
        mapper = { serializationErrorText.desc() }
    ).register<ErrorException, StringDesc> {
        getNetworkErrorExceptionStringDescMapper(
            errorException = it,
            unauthorizedErrorText = unauthorizedErrorText,
            notFoundErrorText = notFoundErrorText,
            accessDeniedErrorText = accessDeniedErrorText,
            internalServerErrorText = internalServerErrorText
        )
    }.register<ValidationException, StringDesc>(::validationExceptionStringDescMapper)
}

/**
 * Maps the input error [exception] to default error text.
 */
private fun getNetworkErrorExceptionStringDescMapper(
    errorException: ErrorException,
    unauthorizedErrorText: StringResource,
    notFoundErrorText: StringResource,
    accessDeniedErrorText: StringResource,
    internalServerErrorText: StringResource
): StringDesc {
    val httpStatusCode = errorException.httpStatusCode
    return when {
        errorException.isUnauthorized -> unauthorizedErrorText.desc()
        errorException.isNotFound -> notFoundErrorText.desc()
        errorException.isAccessDenied -> accessDeniedErrorText.desc()
        httpStatusCode >= HttpStatusCode.InternalServerError.value &&
                httpStatusCode <= INTERNAL_SERVER_ERROR_CODE_MAX -> StringDesc.ResourceFormatted(
            internalServerErrorText,
            httpStatusCode
        )
        else -> errorException.description?.desc() ?: ExceptionMappersStorage.getFallbackValue()
    }
}

/**
 * Converts the validation [exception] to a combination of messages as one [CompositionStringDesc].
 */
private fun validationExceptionStringDescMapper(exception: ValidationException): StringDesc {
    return exception.errors.joinToString(separator = ". ") { it.message }.desc()
}
