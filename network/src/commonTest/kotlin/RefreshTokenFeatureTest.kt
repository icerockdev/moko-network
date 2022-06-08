/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.plugins.RefreshTokenFeature
import dev.icerock.moko.network.plugins.TokenPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class RefreshTokenFeatureTest {
    @Test
    fun `refresh token not called when request credentials not actual`() {
        val isFirstRequestHolder = MutableStateFlow<Boolean>(true)
        val client = createMockClient(
            featureConfig = {
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
            client.get<HttpResponse>("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
    }

    @Test
    fun `refresh token called when request credentials actual`() {
        val invalidToken = "123"
        val validToken = "124"
        val tokenHolder = MutableStateFlow<String?>(invalidToken)
        val client = createMockClient(
            tokenProvider = object : TokenPlugin.TokenProvider {
                override fun getToken(): String? {
                    return tokenHolder.value
                }
            },
            featureConfig = {
                this.updateTokenHandler = {
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

        val result = runBlocking {
            client.get<HttpResponse>("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
        assertEquals(expected = validToken, actual = result.request.headers[AUTH_HEADER_NAME])
    }

    private fun createMockClient(
        tokenProvider: TokenPlugin.TokenProvider? = null,
        featureConfig: RefreshTokenFeature.Config.() -> Unit,
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
            install(RefreshTokenFeature, featureConfig)
        }
    }

    private companion object {
        const val AUTH_HEADER_NAME = "Auth"
    }
}
