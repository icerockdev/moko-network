/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineCapability
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.callContext
import io.ktor.client.features.websocket.WebSocketCapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.isUpgradeRequest
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSURL
import kotlin.coroutines.CoroutineContext

class WSIosHttpClientEngine(
    private val wrappedEngine: HttpClientEngine
) : HttpClientEngine {

    override val supportedCapabilities: Set<HttpClientEngineCapability<*>>
        get() = wrappedEngine.supportedCapabilities + setOf(WebSocketCapability)

    override val config: HttpClientEngineConfig
        get() = wrappedEngine.config

    override val dispatcher: CoroutineDispatcher
        get() = wrappedEngine.dispatcher

    override val coroutineContext: CoroutineContext
        get() = wrappedEngine.coroutineContext

    @InternalAPI
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        println("execute $data")

        val callContext = callContext()
        return if (data.isUpgradeRequest()) {
            executeWebSocketRequest(data, callContext)
        } else {
            wrappedEngine.execute(data)
        }
    }

    private suspend fun executeWebSocketRequest(
        data: HttpRequestData,
        callContext: CoroutineContext
    ): HttpResponseData {
        val requestTime = GMTDate()
        val url: String = data.url.toString()
        val socketEndpoint = NSURL.URLWithString(url)!!

        println("start session to $socketEndpoint")
        val session = IosWebSocket(socketEndpoint, callContext).apply { start() }

        val originResponse = session.originResponse.await()
        println("opened protocol: $originResponse")

        return HttpResponseData(
            statusCode = HttpStatusCode.OK,
            requestTime = requestTime,
            headers = Headers.Empty,
            version = HttpProtocolVersion.HTTP_1_0, // read from originResponse
            body = session,
            callContext = callContext
        )
    }

    override fun close() {
        wrappedEngine.close()
    }
}
