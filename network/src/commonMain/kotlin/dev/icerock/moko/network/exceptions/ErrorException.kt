/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse

class ErrorException(
    request: HttpRequest,
    response: HttpResponse,
    val code: Int,
    val description: String?
) : ResponseException(request, response, description.orEmpty()) {
    override val message: String?
        get() = description ?: super.message
}
