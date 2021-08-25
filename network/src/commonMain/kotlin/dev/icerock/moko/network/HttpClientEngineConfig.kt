/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.engine.HttpClientEngine

class HttpClientEngineConfig {
    var androidConnectTimeoutSeconds: Long? = null
    var androidCallTimeoutSeconds: Long? = null
    var androidReadTimeoutSeconds: Long? = null
    var androidWriteTimeoutSeconds: Long? = null
    var iosTimeoutIntervalForRequest: Double? = null
    var iosTimeoutIntervalForResource: Double? = null
}

expect fun createHttpClientEngine(block: HttpClientEngineConfig.() -> Unit = {}): HttpClientEngine

expect fun createHttpClientEngine(): HttpClientEngine