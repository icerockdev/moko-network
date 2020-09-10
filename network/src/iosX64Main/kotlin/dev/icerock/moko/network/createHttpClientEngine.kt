/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios

actual fun createHttpClientEngine(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
    val config = HttpClientEngineConfig().also(block)
    return Ios.create {
        this.configureSession {
            config.iosTimeoutIntervalForRequest?.let { setTimeoutIntervalForRequest(it) }
            config.iosTimeoutIntervalForResource?.let { setTimeoutIntervalForResource(it) }
        }
    }
}
