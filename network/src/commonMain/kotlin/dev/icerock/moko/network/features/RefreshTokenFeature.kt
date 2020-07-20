/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.features

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import kotlinx.coroutines.sync.Mutex

class RefreshTokenFeature(
    private val updateTokenHandler: suspend () -> Boolean,
    private val getCurrentToken: () -> String
) {

    class Config {
        var updateTokenHandler: (suspend () -> Boolean)? = null
        var getCurrentToken: (() -> String)? = null

        fun build() = RefreshTokenFeature(
            updateTokenHandler ?: throw IllegalArgumentException("updateTokenHandler should be passed"),
            getCurrentToken ?: throw IllegalArgumentException("getCurrentToken should be passed")
        )
    }

    companion object Feature : HttpClientFeature<Config, RefreshTokenFeature> {

        private val refreshTokenHttpFeatureMutex = Mutex()

        override val key = AttributeKey<RefreshTokenFeature>("RefreshTokenFeature")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(feature: RefreshTokenFeature, scope: HttpClient) {
            scope.receivePipeline.intercept(HttpReceivePipeline.After) {
                if (context.response.status == HttpStatusCode.Unauthorized) {
                    val currentToken = feature.getCurrentToken()

                    refreshTokenHttpFeatureMutex.lock()

                    // If the local current token is already changed, then the feature will
                    // repeat the previous request
                    if (currentToken != feature.getCurrentToken()) {
                        refreshTokenHttpFeatureMutex.unlock()
                        val requestBuilder = HttpRequestBuilder().takeFrom(context.request)
                        val result: HttpResponse = context.client.request(requestBuilder)
                        proceedWith(result)
                    } else {
                        // If the local current token has not been changed, then will be called
                        // update callback and repeat the previous request if update was successful
                        if(feature.updateTokenHandler.invoke()) {
                            refreshTokenHttpFeatureMutex.unlock()
                            val requestBuilder = HttpRequestBuilder().takeFrom(context.request)
                            val result: HttpResponse = context.client.request(requestBuilder)
                            proceedWith(result)
                        }
                        // If the token update wasn't successful
                        else {
                            refreshTokenHttpFeatureMutex.unlock()
                            proceedWith(it)
                        }
                    }
                } else {
                    proceedWith(it)
                }
            }
        }
    }
}
