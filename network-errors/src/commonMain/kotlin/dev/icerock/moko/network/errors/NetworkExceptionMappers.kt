/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.errors

import dev.icerock.moko.errors.mappers.BasicException
import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ValidationException
import dev.icerock.moko.network.isNetworkConnectionError
import dev.icerock.moko.resources.desc.CompositionStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

/**
 * Registers all default exception mappers of the network module to [ExceptionMappersStorage].
 */
fun ExceptionMappersStorage.registerAllNetworkMappers(): ExceptionMappersStorage {
    return condition(
        condition = ::networkConnectionErrorCondition,
        mapper = ::networkConnectionErrorStringDescMapper
    ).register(::networkErrorExceptionStringDescMapper)
        .register(::validationExceptionStringDescMapper)
}

/**
 * Checks whether the input [exception] is a network connection error.
 */
fun networkConnectionErrorCondition(exception: BasicException): Boolean {
    return exception.isNetworkConnectionError()
}

/**
 * Maps the input [exception] to default network connection error text.
 */
fun networkConnectionErrorStringDescMapper(exception: BasicException): StringDesc {
    return MR.strings.networkConnectionErrorText.desc()
}

/**
 * Maps the input error [exception] to default error text.
 */
fun networkErrorExceptionStringDescMapper(exception: ErrorException): StringDesc {
    val httpStatusCode = exception.httpStatusCode
    return when {
        exception.isUnauthorized -> MR.strings.unauthorizedErrorText.desc()
        exception.isNotFound -> MR.strings.unauthorizedErrorText.desc()
        exception.isAccessDenied -> MR.strings.unauthorizedErrorText.desc()
        httpStatusCode >= 500 && httpStatusCode < 600 -> MR.strings.internalServerErrorText.desc()
        else -> exception.description?.desc() ?: ExceptionMappersStorage.unknownErrorText
    }
}

/**
 * Converts the validation [exception] to a combination of messages as one [CompositionStringDesc].
 */
fun validationExceptionStringDescMapper(exception: ValidationException): StringDesc {
    val errorMessages = exception.errors.map { it.message.desc() }
    return CompositionStringDesc(errorMessages, ". ")
}
