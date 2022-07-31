/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import kotlinx.coroutines.sync.Mutex

class RefreshTokenPlugin(
    private val updateTokenHandler: suspend () -> Boolean,
    private val isCredentialsActual: (HttpRequest) -> Boolean
) {

    class Config {
        var updateTokenHandler: (suspend () -> Boolean)? = null
        var isCredentialsActual: ((HttpRequest) -> Boolean)? = null

        fun build() = RefreshTokenPlugin(
            updateTokenHandler
                ?: throw IllegalArgumentException("updateTokenHandler should be passed"),
            isCredentialsActual
                ?: throw IllegalArgumentException("isCredentialsActual should be passed")
        )
    }

    companion object Plugin : HttpClientPlugin<Config, RefreshTokenPlugin> {

        private val refreshTokenHttpPluginMutex = Mutex()

        override val key = AttributeKey<RefreshTokenPlugin>("RefreshTokenPlugin")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(plugin: RefreshTokenPlugin, scope: HttpClient) {
            scope.receivePipeline.intercept(HttpReceivePipeline.After) {
                if (subject.status != HttpStatusCode.Unauthorized) {
                    proceedWith(subject)
                    return@intercept
                }

                refreshTokenHttpPluginMutex.lock()

                // If token of the request isn't actual, then token has already been updated and
                // let's just to try repeat request
                if (!plugin.isCredentialsActual(subject.request)) {
                    refreshTokenHttpPluginMutex.unlock()
                    val requestBuilder = HttpRequestBuilder().takeFrom(subject.request)
                    val result: HttpResponse = scope.request(requestBuilder)
                    proceedWith(result)
                    return@intercept
                }

                // Else if token of the request is actual (same as in the storage), then need to send
                // refresh request.
                if (plugin.updateTokenHandler.invoke()) {
                    // If the request refresh was successful, then let's just to try repeat request
                    refreshTokenHttpPluginMutex.unlock()
                    val requestBuilder = HttpRequestBuilder().takeFrom(subject.request)
                    val result: HttpResponse = scope.request(requestBuilder)
                    proceedWith(result)
                } else {
                    // If the request refresh was unsuccessful
                    refreshTokenHttpPluginMutex.unlock()
                    proceedWith(subject)
                }
            }
        }
    }
}
