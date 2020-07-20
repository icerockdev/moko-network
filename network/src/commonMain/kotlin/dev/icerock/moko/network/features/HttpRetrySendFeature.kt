/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.features

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpStatement
import io.ktor.util.AttributeKey
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.delay

/**
@param delayGetter the function for calculation a delay between failed request and next request
@param maxAmountRetrying the max amounts retrying requests
@param isShouldRetryRequest the function for condition a retrying request. By default it check `e is IOException `
 */
class HttpRetrySendFeature(
    private val delayGetter: (lastDelayInMillisecond: Long, timeRetrying: Int) -> Long,
    private val maxAmountsRetrying: Int,
    private val isShouldRetryRequest: (e: Throwable) -> Boolean
) {

    class Config {
        var onGetDelay: (lastDelayInMillisecond: Long, timeRetrying: Int) -> Long =
            { _, _ -> 2_000L }
        var maxAmountRetrying: Int = 3 // first request + three retrying requests,
        var onConditionRetrying: (e: Throwable) -> Boolean = { e -> e is IOException }
        fun build() = HttpRetrySendFeature(onGetDelay, maxAmountRetrying, onConditionRetrying)
    }

    companion object Feature : HttpClientFeature<Config, HttpRetrySendFeature> {
        private const val LAST_DELAY_HEADER = "HttpRetrySendFeature-Last-Delay"
        private const val RETRY_COUNTER_HEADER = "HttpRetrySendFeature-Retry-Counter"

        override val key: AttributeKey<HttpRetrySendFeature> = AttributeKey("HttpRetrySendFeature")

        override fun install(feature: HttpRetrySendFeature, scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.Before) {
                val counter = context.headers[RETRY_COUNTER_HEADER]?.toInt() ?: 0
                val lastDelay = context.headers[LAST_DELAY_HEADER]?.toLong()
                    ?: feature.delayGetter(0L, counter)
                if (counter < feature.maxAmountsRetrying) {
                    try {
                        context.headers.remove(LAST_DELAY_HEADER)
                        context.headers.remove(RETRY_COUNTER_HEADER)
                        proceed()
                    } catch (e: Throwable) {
                        if (feature.isShouldRetryRequest(e)) {
                            val requestBuilder = HttpRequestBuilder().takeFrom(context)
                            val indexRetrying = counter + 1
                            val nextDelay = feature.delayGetter(lastDelay, counter)
                            requestBuilder.headers[RETRY_COUNTER_HEADER] = indexRetrying.toString()
                            requestBuilder.headers[LAST_DELAY_HEADER] = nextDelay.toString()
                            delay(nextDelay)
                            finish()

                            proceedWith(HttpStatement(requestBuilder, scope).execute().call)
                        } else {
                            throw e
                        }
                    }
                } else {
                    context.headers.remove(LAST_DELAY_HEADER)
                    context.headers.remove(RETRY_COUNTER_HEADER)
                    proceed()
                }

            }
        }

        override fun prepare(block: Config.() -> Unit): HttpRetrySendFeature {
            return Config().apply(block).build()
        }

    }
}
