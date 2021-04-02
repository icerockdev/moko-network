/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.content.TextContent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import openapi.anyof.apis.DefaultApi
import openapi.anyof.models.PetByAge
import openapi.anyof.models.PetByType
import openapi.anyof.models.PetsPatchRequestBodyComposed
import openapi.anyof.models.PetsPatchResponse200Composed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnyOfTest {
    @Test
    fun `anyOf - both items`() {
        val anyOfApi = createAnyOfApi { request ->
            val body = request.body
            assertTrue(body is TextContent)
            assertEquals(
                expected = """{"age":4,"pet_type":"Cat","hunts":true}""",
                actual = body.text
            )
            respondOk(body.text)
        }

        val petByAge = PetByAge(age = 4)
        val petByType = PetByType(hunts = true, petType = PetByType.PetType.CAT)

        val result = runBlocking {
            anyOfApi.petsPatch(
                PetsPatchRequestBodyComposed(item0 = petByAge, item1 = petByType)
            )
        }

        assertEquals(
            expected = PetsPatchResponse200Composed(item0 = petByAge, item1 = petByType),
            actual = result
        )
    }

    @Test
    fun `anyOf - first item`() {
        val anyOfApi = createAnyOfApi { request ->
            val body = request.body
            assertTrue(body is TextContent)
            assertEquals(
                expected = """{"age":4}""",
                actual = body.text
            )
            respondOk(body.text)
        }

        val petByAge = PetByAge(age = 4)

        val result = runBlocking {
            anyOfApi.petsPatch(
                PetsPatchRequestBodyComposed(item0 = petByAge, item1 = null)
            )
        }

        assertEquals(
            expected = PetsPatchResponse200Composed(item0 = petByAge, item1 = null),
            actual = result
        )
    }

    @Test
    fun `anyOf - second item`() {
        val anyOfApi = createAnyOfApi { request ->
            val body = request.body
            assertTrue(body is TextContent)
            assertEquals(
                expected = """{"pet_type":"Cat","hunts":true}""",
                actual = body.text
            )
            respondOk(body.text)
        }

        val petByType = PetByType(hunts = true, petType = PetByType.PetType.CAT)

        val result = runBlocking {
            anyOfApi.petsPatch(
                PetsPatchRequestBodyComposed(item0 = null, item1 = petByType)
            )
        }

        assertEquals(
            expected = PetsPatchResponse200Composed(item0 = null, item1 = petByType),
            actual = result
        )
    }

    private fun createAnyOfApi(mock: MockRequestHandler): DefaultApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return DefaultApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}
