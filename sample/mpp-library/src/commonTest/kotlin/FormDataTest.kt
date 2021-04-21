/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import cases.formData.apis.AuthApi
import cases.formData.models.Response
import cases.formData.models.SignupRequest
import io.ktor.client.engine.mock.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormDataTest {
    @Test
    fun `formData body`() {
        val api = createApi { request ->
            val body = request.body
            assertTrue(body is FormDataContent)
            assertEquals(expected = "SignupRequest(firstName=first, lastName=last, phone=+799, email=a@b, password=111, passwordRepeat=111, countryId=1, cityId=2, company=test, middleName=null, post=null, interests=null)", actual = body.formData["signup"])
            assertEquals(expected = "teststring", actual = body.formData["avatar"])

            respondOk(
                """
{
    "status": 200,
    "message": "ok",
    "timestamp": 1001.0,
    "success": true
}
            """.trimIndent()
            )
        }

        val result = runBlocking {
            api.signup(
                signup = SignupRequest(
                    firstName = "first",
                    lastName = "last",
                    phone = "+799",
                    email = "a@b",
                    password = "111",
                    passwordRepeat = "111",
                    countryId = 1,
                    cityId = 2,
                    company = "test"
                ),
                avatar = "teststring"
            )
        }

        assertEquals(
            expected = Response(
                status = 200,
                message = "ok",
                timestamp = 1001.0,
                success = true
            ),
            actual = result
        )
    }

    private fun createApi(mock: MockRequestHandler): AuthApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return AuthApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}
