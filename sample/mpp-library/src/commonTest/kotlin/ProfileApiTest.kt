/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptionfactory.parser.ErrorExceptionParser
import dev.icerock.moko.network.exceptionfactory.parser.ValidationExceptionParser
import dev.icerock.moko.network.features.ExceptionFeature
import dev.icerock.moko.network.generated.apis.ProfileApi
import dev.icerock.moko.network.generated.models.UserInfo
import dev.icerock.moko.network.generated.models.UserSettingsNullableSchema
import dev.icerock.moko.network.generated.models.UserStateEnum
import dev.icerock.moko.network.nullable.asNullable
import dev.icerock.moko.test.runBlocking
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
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
        json = Json.Default
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

    @Test
    fun `test enum array`() {
        val testResponse = """
            {
                "values" : ["Break", "Meal", "Unscheduled"]
            }
        """.trimIndent()

        val httpClient = createMockClient {
            respondOk(content = testResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        val result = runBlocking {
            profileApi.profileInfoEnumarray()
        }

        assertNotNull(result.values)

        assertTrue {
            result.values!!.size == 3
        }
    }

    @Test
    fun `test enum with null`() {
        val testResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient {
            respondOk(content = testResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        val result = runBlocking {
            profileApi.profileInfoState()
        }

        assertNotNull(result.stateName)

        assertTrue {
            result.stateName?.value == UserStateEnum.StateName._1
        }
    }

    @Test
    fun `test non-required Nullable property must not exist`() {
        val isTestSuccessFlow = MutableStateFlow(false)

        json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }

        val successResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient { request ->
            val inputString = request.body.toByteArray().decodeToString()

            isTestSuccessFlow.value = inputString.contains("work_id_nonrequired_nullable") == false

            respondOk(content = successResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        // request body must not contain "work_id_nonrequired_nullable" property at all
        val result = runBlocking {
            profileApi.profileInfoStateIdPut(
                id = "10",
                userSettingsNullableSchema = UserSettingsNullableSchema(
                    aboutMeRequiredNullable = null,
                    raitingRequiredNonnull = 10
                )
            )
        }

        assertNotNull(result.stateName)
        assertTrue(isTestSuccessFlow.value)
    }

    @Test
    fun `test non-required Nullable property must contain null`() {
        val isTestSuccessFlow = MutableStateFlow(false)

        json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }

        val successResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient { request ->
            val inputString = request.body.toByteArray().decodeToString()

            isTestSuccessFlow.value = inputString.contains("\"work_id_nonrequired_nullable\":null")

            respondOk(content = successResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        // request body must contain "work_id_nonrequired_nullable" property
        val result = runBlocking {
            profileApi.profileInfoStateIdPut(
                id = "10",
                userSettingsNullableSchema = UserSettingsNullableSchema(
                    aboutMeRequiredNullable = null,
                    raitingRequiredNonnull = 10,
                    workIdNonrequiredNullable = null.asNullable()
                )
            )
        }

        assertNotNull(result.stateName)
        assertTrue(isTestSuccessFlow.value)
    }

    @Test
    fun `test non-required Nullable property must contain value`() {
        val isTestSuccessFlow = MutableStateFlow(false)

        json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }

        val successResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient { request ->
            val inputString = request.body.toByteArray().decodeToString()
            val inputObj = json
                .decodeFromString(UserSettingsNullableSchema.serializer(), inputString)

            isTestSuccessFlow.value = inputObj.workIdNonrequiredNullable != null &&
                    inputObj.workIdNonrequiredNullable!!.value == 10

            respondOk(content = successResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        // request body must contain "work_id_nonrequired_nullable" property
        val result = runBlocking {
            profileApi.profileInfoStateIdPut(
                id = "10",
                userSettingsNullableSchema = UserSettingsNullableSchema(
                    aboutMeRequiredNullable = null,
                    raitingRequiredNonnull = 10,
                    workIdNonrequiredNullable = 10.asNullable()
                )
            )
        }

        assertNotNull(result.stateName)
        assertTrue(isTestSuccessFlow.value)
    }

    @Test
    fun `test non-required Nullable list property must not exist`() {
        val isTestSuccessFlow = MutableStateFlow(false)

        json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }

        val successResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient { request ->
            val inputString = request.body.toByteArray().decodeToString()

            isTestSuccessFlow.value = inputString.contains("array_nonrequired_nullable") == false

            respondOk(content = successResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        // request body must not contain "array_nonrequired_nullable" property at all
        val result = runBlocking {
            profileApi.profileInfoStateIdPut(
                id = "10",
                userSettingsNullableSchema = UserSettingsNullableSchema(
                    aboutMeRequiredNullable = null,
                    raitingRequiredNonnull = 10
                )
            )
        }

        assertNotNull(result.stateName)
        assertTrue(isTestSuccessFlow.value)
    }

    @Test
    fun `test non-required Nullable array property must be null`() {
        val isTestSuccessFlow = MutableStateFlow(false)

        json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }

        val successResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient { request ->
            val inputString = request.body.toByteArray().decodeToString()

            isTestSuccessFlow.value = inputString.contains("\"array_nonrequired_nullable\":null")

            respondOk(content = successResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        // request body must contain "array_nonrequired_nullable" property with null
        val result = runBlocking {
            profileApi.profileInfoStateIdPut(
                id = "10",
                userSettingsNullableSchema = UserSettingsNullableSchema(
                    aboutMeRequiredNullable = null,
                    raitingRequiredNonnull = 10,
                    arrayNonrequiredNullable = null.asNullable()
                )
            )
        }

        assertNotNull(result.stateName)
        assertTrue(isTestSuccessFlow.value)
    }

    @Test
    fun `test non-required Nullable array property must contain values`() {
        val isTestSuccessFlow = MutableStateFlow(false)

        json = Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }

        val successResponse = """
            {
                "state_name" : "state_1"
            }
        """.trimIndent()

        val httpClient = createMockClient { request ->
            val inputString = request.body.toByteArray().decodeToString()
            val inputObj = json
                .decodeFromString(UserSettingsNullableSchema.serializer(), inputString)

            isTestSuccessFlow.value = inputObj.arrayNonrequiredNullable != null &&
                    inputObj.arrayNonrequiredNullable!!.value!!.contains("123")

            respondOk(content = successResponse)
        }
        val profileApi = ProfileApi(httpClient = httpClient, json = json)

        // request body must contain "array_nonrequired_nullable" property with values
        val result = runBlocking {
            profileApi.profileInfoStateIdPut(
                id = "10",
                userSettingsNullableSchema = UserSettingsNullableSchema(
                    aboutMeRequiredNullable = null,
                    raitingRequiredNonnull = 10,
                    arrayNonrequiredNullable = listOf("123").asNullable()
                )
            )
        }

        assertNotNull(result.stateName)
        assertTrue(isTestSuccessFlow.value)
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
