/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import dev.icerock.moko.network.exceptionfactory.ExceptionFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.readText

class ExceptionPlugin(private val exceptionFactory: ExceptionFactory) {

    class Config {
        var exceptionFactory: ExceptionFactory? = null
        fun build() = ExceptionPlugin(
            exceptionFactory
                ?: throw IllegalArgumentException("Exception factory should be contain")
        )
    }

    companion object Plugin : HttpClientPlugin<Config, ExceptionPlugin> {

        override val key = AttributeKey<ExceptionPlugin>("ExceptionPlugin")

        override fun prepare(block: Config.() -> Unit) = Config().apply(block).build()

        override fun install(plugin: ExceptionPlugin, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { (_, body) ->
                if (body !is ByteReadChannel) return@intercept

                val response = context.response
                if (!response.status.isSuccess()) {
                    val packet = body.readRemaining()
                    val responseString = packet.readText(charset = Charset.forName("UTF-8"))
                    throw plugin.exceptionFactory.createException(
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
