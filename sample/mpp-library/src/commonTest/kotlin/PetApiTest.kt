/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.generated.apis.PetApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import tests.utils.readResourceText
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PetApiTest {
    private lateinit var httpClient: HttpClient
    private lateinit var json: Json
    private lateinit var petApi: PetApi

    @BeforeTest
    fun setup() {
        json = Json.Default

        httpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respondOk(content = readResourceText("PetstoreSearchResponse.json"))
                }
            }
        }

        petApi = PetApi(
            httpClient = httpClient,
            json = json,
            basePath = "https://localhost"
        )
    }

    @Test
    fun `search test`() {
        val result = runBlocking {
            petApi.findPetsByStatus(listOf("available"))
        }

        assertEquals(expected = 217, actual = result.size)
    }
}