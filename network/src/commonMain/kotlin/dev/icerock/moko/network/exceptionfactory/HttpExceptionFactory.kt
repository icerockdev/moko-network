/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptionfactory

import dev.icerock.moko.network.exceptions.ResponseException
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse

class HttpExceptionFactory(
    private val defaultParser: HttpExceptionParser,
    private val customParsers: Map<Int, HttpExceptionParser>
) : ExceptionFactory {

    override fun createException(
        request: HttpRequest,
        response: HttpResponse,
        responseBody: String?
    ): ResponseException {
        val parser = customParsers[response.status.value] ?: defaultParser

        val exception = parser.parseException(request, response, responseBody)

        return exception ?: ResponseException(request, response, responseBody.orEmpty())
    }

    interface HttpExceptionParser {
        fun parseException(
            request: HttpRequest,
            response: HttpResponse,
            responseBody: String?
        ): ResponseException?
    }
}
