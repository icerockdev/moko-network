/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.nullable.asNullable
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import openapi.anyType.apis.DefaultApi
import openapi.anyType.models.Resp
import kotlin.test.Test
import kotlin.test.assertEquals

class AnyTypeTest {
    @Test
    fun `any type in response`() {
        val api = createApi {
            respondOk(
                """
{
    "anyProp": "test",
    "anyList": [
        "test2",
        3,
        {
            "name": "none"
        },
        null
    ]
}
            """.trimIndent()
            )
        }

        val result = runBlocking {
            api.dynamicGet()
        }

        assertEquals(
            expected = Resp(
                anyProp = JsonPrimitive("test").asNullable(),
                anyList = listOf(
                    JsonPrimitive("test2"),
                    JsonPrimitive(3),
                    buildJsonObject {
                        put("name", "none")
                    },
                    JsonNull
                )
            ),
            actual = result
        )
    }

    private fun createApi(mock: MockRequestHandler): DefaultApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return DefaultApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}
