/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptionfactory

import dev.icerock.moko.network.exceptions.ResponseException

class HttpExceptionFactory(
    private val defaultParser: HttpExceptionParser,
    private val customParsers: Map<Int, HttpExceptionParser>
) : ExceptionFactory {
    override fun createException(httpStatusCode: Int, responseBody: String?): ResponseException {
        val parser = customParsers[httpStatusCode] ?: defaultParser

        val exception = parser.parseException(httpStatusCode, responseBody)

        return exception ?: ResponseException(httpStatusCode, responseBody.orEmpty())
    }

    interface HttpExceptionParser {
        fun parseException(httpCode: Int, responseBody: String?): ResponseException?
    }
}
