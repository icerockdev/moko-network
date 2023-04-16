/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.util.AttributeKey

class DynamicUserAgent(
    val agentProvider: () -> String?
) {
    class Config(var agentProvider: () -> String? = { null })

    companion object Feature : HttpClientPlugin<Config, DynamicUserAgent> {
        override val key: AttributeKey<DynamicUserAgent> = AttributeKey("DynamicUserAgent")

        override fun prepare(block: Config.() -> Unit): DynamicUserAgent =
            DynamicUserAgent(Config().apply(block).agentProvider)

        override fun install(plugin: DynamicUserAgent, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                plugin.agentProvider()?.let { context.header(HttpHeaders.UserAgent, it) }
            }
        }
    }
}
