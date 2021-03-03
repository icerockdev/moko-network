/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptionfactory.parser.ErrorExceptionParser
import dev.icerock.moko.network.exceptionfactory.parser.ValidationExceptionParser
import dev.icerock.moko.network.features.ExceptionFeature
import dev.icerock.moko.network.generated.apis.ProfileApi
import dev.icerock.moko.network.generated.models.UserInfo
import dev.icerock.moko.test.runBlocking
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProfileApiTest {
    private lateinit var json: Json

    @BeforeTest
    fun setup() {
        json = Json {
            ignoreUnknownKeys = true
        }
    }

    @Test
    fun `test profileNotification with json field`() {
        val testResponse = """
            {
                "id" : 0,
                "content" : {
                    "field" : "json"
                },
                "userInfo" : {
                    "name" : "name"
                }
            }
        """.trimIndent()
        val httpClient = createMockClient {
            respondOk(content = testResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        val result = runBlocking {
            profileApi.profileNotification()
        }

        assertNotNull(result.userInfo)

        val userInfo = json.decodeFromJsonElement(UserInfo.serializer(), result.userInfo!!)

        assertTrue {
            userInfo.name == "name"
        }
    }

    @Test
    fun `test profileNotification with string field`() {
        val testResponse = """
            {
                "id" : 0,
                "content" : {
                    "field" : "json"
                },
                "userInfo" : "name"
            }
        """.trimIndent()

        val httpClient = createMockClient {
            respondOk(content = testResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)


        val result = runBlocking {
            profileApi.profileNotification()
        }

        assertNotNull(result.userInfo)

        val userInfo = json.decodeFromJsonElement(String.serializer(), result.userInfo!!)

        assertTrue {
            userInfo == "name"
        }
    }

    private fun createMockClient(
        handler: MockRequestHandler
    ): HttpClient {
        val json = Json {
            ignoreUnknownKeys = true
        }

        return HttpClient(MockEngine) {
            engine {
                addHandler(handler)
            }

            install(ExceptionFeature) {
                exceptionFactory = HttpExceptionFactory(
                    defaultParser = ErrorExceptionParser(json),
                    customParsers = mapOf(
                        HttpStatusCode.UnprocessableEntity.value to ValidationExceptionParser(json)
                    )
                )
            }

            expectSuccess = false
        }
    }
}
