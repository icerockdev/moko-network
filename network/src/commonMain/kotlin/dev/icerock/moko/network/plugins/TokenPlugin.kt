/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey

class TokenPlugin private constructor(
    private val tokenHeaderName: String,
    private val tokenProvider: TokenProvider
) {

    class Config {
        var tokenHeaderName: String? = null
        var tokenProvider: TokenProvider? = null
        fun build() = TokenPlugin(
            tokenHeaderName ?: throw IllegalArgumentException("HeaderName should be contain"),
            tokenProvider ?: throw IllegalArgumentException("TokenProvider should be contain")
        )
    }

    companion object Plugin : HttpClientPlugin<Config, TokenPlugin> {
        override val key = AttributeKey<TokenPlugin>("TokenPlugin")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(plugin: TokenPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                plugin.tokenProvider.getToken()?.apply {
                    context.headers.remove(plugin.tokenHeaderName)
                    context.header(plugin.tokenHeaderName, this)
                }
            }
        }
    }

    interface TokenProvider {
        fun getToken(): String?
    }
}
