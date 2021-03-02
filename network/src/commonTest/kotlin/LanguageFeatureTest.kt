/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.features.LanguageFeature
import dev.icerock.moko.network.features.TokenFeature
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

class LanguageFeatureTest {
    @Test
    fun `language added when exist`() {
        val client = createMockClient(
            provider = object : LanguageFeature.LanguageCodeProvider {
                override fun getLanguageCode(): String = "ru"
            },
            handler = { request ->
                if (request.headers[LANGUAGE_HEADER_NAME] == "ru") respondOk()
                else respondBadRequest()
            }
        )

        val result = runBlocking {
            client.get<HttpResponse>("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
    }

    @Test
    fun `language not added when not exist`() {
        val client = createMockClient(
            provider = object : LanguageFeature.LanguageCodeProvider {
                override fun getLanguageCode(): String? = null
            },
            handler = { request ->
                if (request.headers.contains(LANGUAGE_HEADER_NAME).not()) respondOk()
                else respondBadRequest()
            }
        )

        val result = runBlocking {
            client.get<HttpResponse>("localhost")
        }

        assertEquals(expected = HttpStatusCode.OK, actual = result.status)
    }

    private fun createMockClient(
        provider: LanguageFeature.LanguageCodeProvider,
        handler: MockRequestHandler
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler(handler)
            }

            install(LanguageFeature) {
                this.languageHeaderName = LANGUAGE_HEADER_NAME
                this.languageCodeProvider = provider
            }
        }
    }

    private companion object {
        const val LANGUAGE_HEADER_NAME = "Lang"
    }
}
