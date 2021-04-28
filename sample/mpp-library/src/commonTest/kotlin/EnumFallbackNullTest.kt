/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import cases.enumfallback.apis.DefaultApi
import cases.enumfallback.models.CarColor
import cases.enumfallback.models.CarColorDefault
import cases.enumfallback.models.CarColorList
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EnumFallbackNullTest {
    @Test
    fun `enum fallback - expected response`() {
        val enumFallbackApi = createEnumFallbackNullApi { request ->
            respondOk(
                """{"color":"red"}"""
            )
        }

        val result = runBlocking {
            enumFallbackApi.carColors()
        }

        println(result)

        assertNotNull(result.item0)
        assertEquals(CarColor.Color.RED, result.item0.color)

        assertNotNull(result.item1)
        assertEquals(CarColorDefault.Color.RED, result.item1.color)
    }

    @Test
    fun `enum fallback - expected list response`() {
        val enumFallbackApi = createEnumFallbackNullApi { request ->
            respondOk(
                """{"color": [ "red", "white" ] }"""
            )
        }

        val result = runBlocking {
            enumFallbackApi.carColorsList()
        }

        println(result)

        assertNotNull(result.color)
        assertTrue { result.color.contains(CarColorList.Color.RED) }
        assertTrue { result.color.contains(CarColorList.Color.WHITE) }
    }

    @Test
    fun `enum fallback - unexpected response`() {
        val enumFallbackApi = createEnumFallbackNullApi { request ->
            respondOk(
                """{"color":"cyan"}"""
            )
        }

        val result = runBlocking {
            enumFallbackApi.carColors()
        }

        println(result)

        assertNotNull(result.item0)
        assertNull(result.item0.color)
    }

    @Test
    fun `enum fallback - unexpected list response`() {
        val enumFallbackApi = createEnumFallbackNullApi { request ->
            respondOk(
                """{"color": [ "cyan", "red" ] }"""
            )
        }

        val result = runBlocking {
            enumFallbackApi.carColorsList()
        }

        println(result)

        assertNotNull(result.color)
        assertTrue { result.color.size == 2 }
    }

    private fun createEnumFallbackNullApi(mock: MockRequestHandler): DefaultApi {
        val json = Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
        }
        val httpClient = createMockClient(json, mock)
        return DefaultApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}
