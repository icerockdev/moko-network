/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.plugins.RefreshTokenPlugin
import dev.icerock.moko.network.plugins.TokenPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class RefreshTokenPluginTest {
    @Test
    fun `refresh token not called when request credentials not actual`() {
        val isFirstRequestHolder = MutableStateFlow<Boolean>(true)
        val client = createMockClient(
            pluginConfig = {
                this.updateTokenHandler = {
                    throw IllegalStateException("update token should not be called at all")
                }
                this.isCredentialsActual = { false }
            },
            handler = {
                if (isFirstRequestHolder.value) {
                    isFirstRequestHolder.value = false
                    respondError(status = HttpStatusCode.Unauthorized)
                } else respondOk()
            }
        )

        val result = runBlocking {
            client.get("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
    }

    @Test
    fun `refresh token called when request credentials actual`() {
        val invalidToken = "123"
        val validToken = "124"
        val tokenHolder = MutableStateFlow<String?>(invalidToken)
        val client = createMockClient(
            tokenProvider = { tokenHolder.value },
            pluginConfig = {
                this.updateTokenHandler = {
                    tokenHolder.value = validToken
                    true
                }
                this.isCredentialsActual = { request ->
                    request.headers[AUTH_HEADER_NAME] == tokenHolder.value
                }
            }
        ) { request ->
            if (request.headers[AUTH_HEADER_NAME] == invalidToken) {
                respondError(status = HttpStatusCode.Unauthorized)
            } else respondOk()
        }

        val result = runBlocking {
            client.get("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
        assertEquals(expected = validToken, actual = result.request.headers[AUTH_HEADER_NAME])
    }

    @Test
    fun `mutex not lock permanently when isCredentialsActual fail`() {
        val invalidToken = "123"
        val validToken = "124"
        val tokenHolder = MutableStateFlow<String?>(invalidToken)
        var isFirstTime = true

        val client = createMockClient(
            tokenProvider = object : TokenPlugin.TokenProvider {
                override fun getToken(): String? {
                    return tokenHolder.value
                }
            },
            pluginConfig = {
                this.updateTokenHandler = {
                    tokenHolder.value = validToken
                    true
                }
                this.isCredentialsActual = { request ->
                    with(request.headers[AUTH_HEADER_NAME] == tokenHolder.value) {
                        if (isFirstTime) {
                            isFirstTime = false
                            throw IOException("simulate io Error")
                        }
                        this
                    }
                }
            },
            handler = { request ->
                if (request.headers[AUTH_HEADER_NAME] == invalidToken) {
                    respondError(status = HttpStatusCode.Unauthorized)
                } else respondOk()
            }
        )

        runCatching {
            runBlocking {
                client.get("localhost")
            }
        }.onFailure {
            println("simulate first request fail")
        }

        val result = runBlocking {
            client.get("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
        assertEquals(expected = validToken, actual = result.request.headers[AUTH_HEADER_NAME])
    }

    @Test
    fun `mutex not lock permanently when updateTokenHandler fail`() {
        val invalidToken = "123"
        val validToken = "124"
        val tokenHolder = MutableStateFlow<String?>(invalidToken)
        var isFirstTime = true

        val client = createMockClient(
            tokenProvider = object : TokenPlugin.TokenProvider {
                override fun getToken(): String? {
                    return tokenHolder.value
                }
            },
            pluginConfig = {
                this.updateTokenHandler = {
                    if (isFirstTime) {
                        isFirstTime = false
                        throw IOException("simulate io Error")
                    }
                    tokenHolder.value = validToken
                    true
                }
                this.isCredentialsActual = { request ->
                    request.headers[AUTH_HEADER_NAME] == tokenHolder.value
                }
            },
            handler = { request ->
                if (request.headers[AUTH_HEADER_NAME] == invalidToken) {
                    respondError(status = HttpStatusCode.Unauthorized)
                } else respondOk()
            }
        )

        runCatching {
            runBlocking {
                client.get("localhost")
            }
        }.onFailure {
            println("simulate first request fail")
        }

        val result = runBlocking {
            client.get("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
        assertEquals(expected = validToken, actual = result.request.headers[AUTH_HEADER_NAME])
    }

    private fun createMockClient(
        tokenProvider: TokenPlugin.TokenProvider? = null,
        pluginConfig: RefreshTokenPlugin.Config.() -> Unit,
        handler: MockRequestHandler
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler(handler)
            }

            if (tokenProvider != null) {
                install(TokenPlugin) {
                    this.tokenHeaderName = AUTH_HEADER_NAME
                    this.tokenProvider = tokenProvider
                }
            }
            install(RefreshTokenPlugin, pluginConfig)
        }
    }

    private companion object {
        const val AUTH_HEADER_NAME = "Auth"
    }
}
