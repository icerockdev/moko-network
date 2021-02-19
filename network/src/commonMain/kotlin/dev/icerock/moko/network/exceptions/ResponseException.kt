/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

open class ResponseException(
    val request: HttpRequest,
    val response: HttpResponse,
    val responseMessage: String
) : Exception("Request: ${request.url}; Response: $responseMessage [$response.status.value]") {

    val httpStatusCode: Int
        get() = response.status.value

    val isUnauthorized: Boolean
        get() = httpStatusCode == HttpStatusCode.Unauthorized.value

    val isAccessDenied: Boolean
        get() = httpStatusCode == HttpStatusCode.Forbidden.value

    val isNotFound: Boolean
        get() = httpStatusCode == HttpStatusCode.NotFound.value
}
