package com.icerockdev.library

import dev.icerock.moko.network.generated.apis.ProfileApi
import dev.icerock.moko.network.generated.models.UserInfo
import dev.icerock.moko.test.runBlocking
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProfileApiTest {
    private lateinit var httpClient: HttpClient
    private lateinit var json: Json

    private lateinit var profileApi: ProfileApi

    private var testResponse: String = ""
    private var isSuccessResponse = false

    @BeforeTest
    fun setup() {
        json = Json {
            ignoreUnknownKeys = true
        }

        httpClient = HttpClient(MockEngine) {
            expectSuccess = false
            engine {
                addHandler { request ->
                    if (isSuccessResponse) {
                        respondOk(content = testResponse)
                    } else {
                        respondError(HttpStatusCode.BadRequest)
                    }
                }
            }
        }

        profileApi = ProfileApi(httpClient = httpClient, json = json)
    }

    @Test
    fun `test profileNotification with json field`() {
        isSuccessResponse = true
        testResponse = """
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
        isSuccessResponse = true
        testResponse = """
            {
                "id" : 0,
                "content" : {
                    "field" : "json"
                },
                "userInfo" : "name"
            }
        """.trimIndent()

        val result = runBlocking {
            profileApi.profileNotification()
        }

        assertNotNull(result.userInfo)

        val userInfo = json.decodeFromJsonElement(String.serializer(), result.userInfo!!)

        assertTrue {
            userInfo == "name"
        }
    }
}
