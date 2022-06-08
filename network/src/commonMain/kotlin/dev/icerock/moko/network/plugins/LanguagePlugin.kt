/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey

class LanguagePlugin private constructor(
    private val languageHeaderName: String,
    private val languageProvider: LanguagePlugin.LanguageCodeProvider
) {
    class Config {
        var languageHeaderName: String? = null
        var languageCodeProvider: LanguageCodeProvider? = null
        fun build() = LanguagePlugin(
            languageHeaderName ?: throw IllegalArgumentException("HeaderName should be contain"),
            languageCodeProvider
                ?: throw IllegalArgumentException("LanguageCodeProvider should be contain")
        )
    }

    companion object Plugin : HttpClientPlugin<Config, LanguagePlugin> {
        override val key = AttributeKey<LanguagePlugin>("LanguagePlugin")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(plugin: LanguagePlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                plugin.languageProvider.getLanguageCode()?.apply {
                    context.header(plugin.languageHeaderName, this)
                }
            }
        }
    }

    interface LanguageCodeProvider {
        fun getLanguageCode(): String?
    }
}
