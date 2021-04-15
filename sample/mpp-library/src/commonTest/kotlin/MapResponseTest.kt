/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import openapi.mapResponse.apis.DefaultApi
import openapi.mapResponse.models.Dog
import kotlin.test.Test
import kotlin.test.assertEquals

class MapResponseTest {
    @Test
    fun `map in response`() {
        val anyOfApi = createMapResponseApi {
            respondOk(
                """
{
    "first": [
        {
            "bark": false, 
            "breed": "test"
        }
    ]
}
            """.trimIndent()
            )
        }

        val result = runBlocking {
            anyOfApi.dynamicGet()
        }

        assertEquals(
            expected = mapOf(
                "first" to listOf(
                    Dog(bark = false, breed = "test")
                )
            ),
            actual = result
        )
    }

    private fun createMapResponseApi(mock: MockRequestHandler): DefaultApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return DefaultApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}
