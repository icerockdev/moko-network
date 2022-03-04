/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.exceptions.ResponseException
import io.ktor.client.HttpClient
import io.ktor.client.call.ReceivePipelineException
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

operator fun Headers.plus(other: Headers): Headers = when {
    this.isEmpty() -> other
    other.isEmpty() -> this
    else -> Headers.build {
        appendAll(this@plus)
        appendAll(other)
    }
}

suspend inline fun <reified Value : Any> HttpClient.createRequest(
    path: String,
    methodType: HttpMethod = HttpMethod.Get,
    body: Any = EmptyContent,
    contentType: ContentType? = null
): Value {
    @Suppress("SwallowedException")
    try {
        return request {
            method = methodType
            url(path)
            if (contentType != null) contentType(contentType)
            this.body = body
        }
    } catch (e: ReceivePipelineException) {
        if (e.cause is ResponseException) {
            throw e.cause
        } else {
            throw e
        }
    }
}

suspend inline fun <reified Value : Any> HttpClient.createJsonRequest(
    path: String,
    methodType: HttpMethod = HttpMethod.Get,
    body: Any = EmptyContent
): Value =
    createRequest(path, methodType, body, ContentType.Application.Json)
