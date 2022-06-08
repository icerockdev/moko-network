/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.plugins.TokenPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenFeatureTest {
    @Test
    fun `token added when exist`() {
        val client = createMockClient(
            tokenProvider = object : TokenPlugin.TokenProvider {
                override fun getToken(): String {
                    return "mytoken"
                }
            },
            handler = { request ->
                if (request.headers[AUTH_HEADER_NAME] == "mytoken") respondOk()
                else respondBadRequest()
            }
        )

        val result = runBlocking {
            client.get<HttpResponse>("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
    }

    @Test
    fun `token not added when not exist`() {
        val client = createMockClient(
            tokenProvider = object : TokenPlugin.TokenProvider {
                override fun getToken(): String? {
                    return null
                }
            },
            handler = { request ->
                if (request.headers.contains(AUTH_HEADER_NAME).not()) respondOk()
                else respondBadRequest()
            }
        )

        val result = runBlocking {
            client.get<HttpResponse>("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
    }

    private fun createMockClient(
        tokenProvider: TokenPlugin.TokenProvider,
        handler: MockRequestHandler
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler(handler)
            }

            install(TokenPlugin) {
                this.tokenHeaderName = AUTH_HEADER_NAME
                this.tokenProvider = tokenProvider
            }
        }
    }

    private companion object {
        const val AUTH_HEADER_NAME = "Auth"
    }
}
