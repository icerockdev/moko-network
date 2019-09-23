/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.features

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey

class TokenFeature private constructor(
    private val mTokenHeaderName: String,
    private val mTokenProvider: TokenProvider
) {

    class Config {
        var tokenHeaderName: String? = null
        var tokenProvider: TokenProvider? = null
        fun build() = TokenFeature(
            tokenHeaderName ?: throw IllegalArgumentException("HeaderName should be contain"),
            tokenProvider ?: throw IllegalArgumentException("TokenProvider should be contain")
        )
    }

    companion object Feature : HttpClientFeature<Config, TokenFeature> {
        override val key = AttributeKey<TokenFeature>("TokenFeature")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(feature: TokenFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                feature.mTokenProvider.getToken()?.apply {
                    context.header(feature.mTokenHeaderName, this)
                }
            }
        }
    }

    interface TokenProvider {
        fun getToken(): String?
    }
}

fun HttpClientConfig<*>.install(
    tokenHeaderName: String,
    tokenProvider: TokenFeature.TokenProvider
) {
    install(TokenFeature) {
        this.tokenHeaderName = tokenHeaderName
        this.tokenProvider = tokenProvider
    }
}