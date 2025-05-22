/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import io.ktor.client.plugins.createClientPlugin
import io.ktor.client.request.header

val TokenPlugin = createClientPlugin(
    name = "TokenPlugin",
    createConfiguration = ::TokenPluginConfig
) {
    val tokenHeaderName = pluginConfig.tokenHeaderName
    val tokenProvider = pluginConfig.tokenProvider

    if (tokenHeaderName == null) {
        throw IllegalArgumentException("HeaderName should be contain")
    }

    if (tokenProvider == null) {
        throw IllegalArgumentException("TokenProvider should be contain")
    }

    onRequest { request, _ ->
        tokenProvider.getToken()?.apply {
            request.headers.remove(tokenHeaderName)
            request.headers.append(tokenHeaderName, this)
        }
    }
}

class TokenPluginConfig {
    var tokenHeaderName: String? = null
    var tokenProvider: TokenProvider? = null

    fun interface TokenProvider {
        fun getToken(): String?
    }
}
