/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import openapi.allof.apis.DefaultApi
import openapi.allof.models.DogAllOf
import openapi.allof.models.DogComposed
import openapi.allof.models.Pet
import kotlin.test.Test
import kotlin.test.assertEquals

class AllOfTest {
    @Test
    fun `allOf - both items`() {
        val allOfApi = createAllOfApi {
            respondOk("""{"pet_type":"Dog","bark":false}""")
        }

        val result = runBlocking { allOfApi.petsPatch() }

        assertEquals(
            expected = DogComposed(
                item0 = Pet(petType = "Dog"),
                item1 = DogAllOf(bark = false)
            ),
            actual = result
        )
    }

    private fun createAllOfApi(mock: MockRequestHandler): DefaultApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return DefaultApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}
