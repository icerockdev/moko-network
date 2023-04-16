/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("Filename")

package dev.icerock.moko.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

actual fun createHttpClientEngine(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
    val config = HttpClientEngineConfig().also(block)
    return OkHttp.create {
        this.config {
            config.androidConnectTimeoutSeconds?.let { connectTimeout(it, TimeUnit.SECONDS) }
            config.androidCallTimeoutSeconds?.let { callTimeout(it, TimeUnit.SECONDS) }
            config.androidReadTimeoutSeconds?.let { readTimeout(it, TimeUnit.SECONDS) }
            config.androidWriteTimeoutSeconds?.let { writeTimeout(it, TimeUnit.SECONDS) }
        }
    }
}
