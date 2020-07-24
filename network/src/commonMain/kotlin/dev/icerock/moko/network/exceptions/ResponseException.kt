/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse

open class ResponseException(
    val request: HttpRequest,
    val response: HttpResponse,
    responseMessage: String
) : Exception("Request: ${request.url}; Response: $responseMessage [$response.status.value]") {

    val httpStatusCode: Int
        get() = response.status.value

    val isUnauthorized: Boolean
        get() = httpStatusCode == 401

    val isAccessDenied: Boolean
        get() = httpStatusCode == 403

    val isNotFound: Boolean
        get() = httpStatusCode == 404
}
