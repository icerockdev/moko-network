/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("Filename")

package dev.icerock.moko.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.DarwinHttpRequestException

actual fun createHttpClientEngine(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
    // configure darwin throwable mapper
    ThrowableToNSErrorMapper.setup { (it as? DarwinHttpRequestException)?.origin }
    // configure darwin engine
    val config = HttpClientEngineConfig().also(block)
    return Darwin.create {
        this.configureSession {
            config.iosTimeoutIntervalForRequest?.let { setTimeoutIntervalForRequest(it) }
            config.iosTimeoutIntervalForResource?.let { setTimeoutIntervalForResource(it) }
        }
    }
}
