/*
 * Copyright 2024 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.plugins

import dev.icerock.moko.network.plugins.TokenPluginConfig.TokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TokenPluginTest {

    private val mockToken = "mockTokenValue"
    private val mockHeaderName = "X-Auth-Token"

    @Test
    fun `token successfully added`() = runTest {
        val mockEngine = MockEngine { request ->
            assertTrue(request.headers.contains(mockHeaderName, mockToken))
            respond("")
        }

        val client = HttpClient(mockEngine) {
            install(TokenPlugin) {
                this.tokenHeaderName = mockHeaderName
                this.tokenProvider = TokenProvider { mockToken }
            }
        }

        client.get("http://localhost/test")
    }

    @Test
    fun `null token`() = runTest {
        val mockEngine = MockEngine { request ->
            assertFalse(request.headers.contains(mockHeaderName))
            respond("")
        }

        val client = HttpClient(mockEngine) {
            install(TokenPlugin) {
                this.tokenHeaderName = mockHeaderName
                this.tokenProvider = TokenProvider { null }
            }
        }

        client.get("http://localhost/test")
    }

    @Test
    fun `config validation - null tokenHeaderName`() = runTest {
        val exception = assertFailsWith<IllegalArgumentException> {
            HttpClient(MockEngine { respond("") }) {
                install(TokenPlugin) {
                    this.tokenHeaderName = null
                    this.tokenProvider = TokenProvider { mockToken }
                }
            }
        }
        assertEquals("HeaderName should be contain", exception.message)
    }

    @Test
    fun `config validation - null tokenProvider`() = runTest {
        val exception = assertFailsWith<IllegalArgumentException> {
            HttpClient(MockEngine { respond("") }) {
                install(TokenPlugin) {
                    this.tokenHeaderName = mockHeaderName
                    this.tokenProvider = null
                }
            }
        }
        assertEquals("TokenProvider should be contain", exception.message)
    }
}
