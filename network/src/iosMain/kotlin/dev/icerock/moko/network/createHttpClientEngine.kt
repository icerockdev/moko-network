package dev.icerock.moko.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios

actual fun createHttpClientEngine(): HttpClientEngine {
    return WSIosHttpClientEngine(Ios.create { })
}