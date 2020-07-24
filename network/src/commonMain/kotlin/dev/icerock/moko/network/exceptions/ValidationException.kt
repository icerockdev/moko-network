/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse

class ValidationException(
    request: HttpRequest,
    response: HttpResponse,
    message: String,
    val errors: List<Error>
) : ResponseException(request, response, message) {
    data class Error(val field: String, val message: String)
}
