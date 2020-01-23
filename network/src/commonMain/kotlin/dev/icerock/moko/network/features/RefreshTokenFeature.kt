/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.features

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.takeFrom
import io.ktor.client.response.HttpReceivePipeline
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey

class RefreshTokenFeature(
        private val updateTokenHandler: suspend () -> Boolean
) {

    class Config {
        var updateTokenHandler: (suspend () -> Boolean)? = null

        fun build() = RefreshTokenFeature(
                updateTokenHandler ?: throw IllegalArgumentException("updateTokenHandler should be passed")
        )
    }

    companion object Feature : HttpClientFeature<Config, RefreshTokenFeature> {

        override val key = AttributeKey<RefreshTokenFeature>("RefreshTokenFeature")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(feature: RefreshTokenFeature, scope: HttpClient) {
            scope.receivePipeline.intercept(HttpReceivePipeline.After) {
                if (context.response.status == HttpStatusCode.Unauthorized && feature.updateTokenHandler()) {
                    val requestBuilder = HttpRequestBuilder().takeFrom(context.request)
                    proceedWith(context.client.execute(requestBuilder).response)
                } else {
                    proceedWith(it)
                }
            }
        }
    }
}

fun HttpClientConfig<*>.install(tokenUpdater: suspend () -> Boolean) {
    install(RefreshTokenFeature) {
        this.updateTokenHandler = tokenUpdater
    }
}