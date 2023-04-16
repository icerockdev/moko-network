/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.statement.HttpResponse

data class NetworkResponse<T>(
    val httpResponse: HttpResponse,
    private val bodyReader: suspend (HttpResponse) -> T
) {
    suspend fun body(): T = bodyReader(httpResponse)
}
