/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.features

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey

class LanguageFeature private constructor(
    private val languageHeaderName: String,
    private val languageProvider: LanguageFeature.LanguageCodeProvider
) {
    class Config {
        var languageHeaderName: String? = null
        var languageCodeProvider: LanguageCodeProvider? = null
        fun build() = LanguageFeature(
            languageHeaderName ?: throw IllegalArgumentException("HeaderName should be contain"),
            languageCodeProvider ?: throw IllegalArgumentException("LanguageCodeProvider should be contain")
        )
    }

    companion object Feature : HttpClientFeature<Config, LanguageFeature> {
        override val key = AttributeKey<LanguageFeature>("LanguageFeature")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(feature: LanguageFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                feature.languageProvider.getLanguageCode()?.apply {
                    context.header(feature.languageHeaderName, this)
                }
            }
        }
    }

    interface LanguageCodeProvider {
        fun getLanguageCode(): String?
    }
}