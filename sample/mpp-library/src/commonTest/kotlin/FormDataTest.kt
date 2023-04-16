/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import cases.formData.apis.AuthApi
import cases.formData.models.Response
import cases.formData.models.SignupRequest
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.core.ByteReadPacket
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormDataTest {
    @Test
    fun `formData body`() {
        val api = createApi { request ->
            val body: OutgoingContent = request.body
            assertTrue(body is MultiPartFormDataContent)

            val content: String = body.toByteArray().decodeToString()
            val contentWithFixedBoundary: String = content
                .replace(body.boundary, "==boundary==")
                .replace("\r\n", "\n")
            assertEquals(
                expected = """--==boundary==
Content-Disposition: form-data; name=signup
Content-Length: 150

{"firstName":"first","lastName":"last","phone":"+799","email":"a@b","password":"111","passwordRepeat":"111","countryId":1,"cityId":2,"company":"test"}
--==boundary==
Content-Disposition: form-data; name=avatar; filename=avatar

ABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABAB
--==boundary==--
""",
                actual = contentWithFixedBoundary
            )

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

        val avatarBytes = ByteArray(100) { index ->
            if (index % 2 == 0) 'A'.toByte()
            else 'B'.toByte()
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
                avatar = ByteReadPacket(avatarBytes)
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
