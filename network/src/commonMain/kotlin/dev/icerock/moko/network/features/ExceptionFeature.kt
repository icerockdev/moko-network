/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.features

import dev.icerock.moko.network.exceptionfactory.ExceptionFactory
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.readRemaining

class ExceptionFeature(private val exceptionFactory: ExceptionFactory) {

    class Config {
        var exceptionFactory: ExceptionFactory? = null
        fun build() = ExceptionFeature(
            exceptionFactory
                ?: throw IllegalArgumentException("Exception factory should be contain")
        )
    }

    companion object Feature : HttpClientFeature<Config, ExceptionFeature> {

        override val key = AttributeKey<ExceptionFeature>("ExceptionFeature")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(feature: ExceptionFeature, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { (_, body) ->
                if (body !is ByteReadChannel) return@intercept

                val response = context.response
                if (!response.status.isSuccess()) {
                    val packet = body.readRemaining()
                    val responseString = packet.readText(charset = Charset.forName("UTF-8"))
                    throw feature.exceptionFactory.createException(
                        request = context.request,
                        response = context.response,
                        responseBody = responseString
                    )
                }
                proceedWith(subject)
            }
        }
    }
}
