/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptionfactory.parser.ErrorExceptionParser
import dev.icerock.moko.network.exceptionfactory.parser.ValidationExceptionParser
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ResponseException
import dev.icerock.moko.network.exceptions.ValidationException
import dev.icerock.moko.network.plugins.ExceptionPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExceptionFeatureTest {
    @Test
    fun `no exceptions on OK`() {
        val client = createMockClient {
            respondOk()
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<HttpResponse>("localhost") }
        }

        assertEquals(expected = false, actual = result.isFailure)
    }

    @Test
    fun `ErrorException on 400 with error body`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.BadRequest,
                content = """
{
    "error": {
        "message": "test error",
        "code": 9
    }
}
                """.trimIndent()
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ErrorException)

        assertEquals(expected = 9, actual = exc.code)
        assertEquals(expected = "test error", actual = exc.message)
    }

    @Test
    fun `ErrorException on 400 with short error body`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.BadRequest,
                content = """
{
    "error": {
        "message": "test error"
    }
}
                """.trimIndent()
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ErrorException)

        assertEquals(expected = 400, actual = exc.code)
        assertEquals(expected = "test error", actual = exc.message)
    }

    @Test
    fun `ErrorException on 400 with shortest error body`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.BadRequest,
                content = """
{
    "error": { }
}
                """.trimIndent()
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ErrorException)

        assertEquals(expected = 400, actual = exc.code)
        assertEquals(
            expected = "Request: http://localhost/localhost; Response:  [HttpResponse[http://localhost/localhost, 400 Bad Request].status.value]",
            actual = exc.message
        )
    }

    @Test
    fun `ErrorException on 400 with unknown body`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.BadRequest,
                content = "{ }"
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ErrorException)

        assertEquals(expected = 400, actual = exc.code)
        assertEquals(
            expected = "Request: http://localhost/localhost; Response:  [HttpResponse[http://localhost/localhost, 400 Bad Request].status.value]",
            actual = exc.message
        )
    }

    @Test
    fun `ValidationException on 422`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.UnprocessableEntity,
                content = """
[
    {
        "field": "email",
        "message": "invalid email"
    },
    {
        "field": "password",
        "message": "invalid password"
    }
]
                """.trimIndent()
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ValidationException)

        assertEquals(expected = listOf(
            ValidationException.Error(
                field = "email",
                message = "invalid email"
            ),
            ValidationException.Error(
                field = "password",
                message = "invalid password"
            )
        ), actual = exc.errors)
    }

    @Test
    fun `ErrorException on 422`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.UnprocessableEntity,
                content = """
{
    "error": {
        "message": "test error",
        "code": 9
    }
}
                """.trimIndent()
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ErrorException)
        assertEquals(expected = 9, actual = exc.code)
        assertEquals(expected = "test error", actual = exc.message)
    }


    @Test
    fun `ResponseException on 422`() {
        val client = createMockClient {
            respondError(
                status = HttpStatusCode.UnprocessableEntity,
                content = """
{
    "error": {
        "message": "test error"
    }
}
                """.trimIndent()
            )
        }

        val result = runBlocking {
            kotlin.runCatching { client.get<String>("localhost") }
        }

        assertEquals(expected = true, actual = result.isFailure)
        val exc = result.exceptionOrNull()
        assertTrue(actual = exc is ResponseException)
        assertEquals(expected = HttpStatusCode.UnprocessableEntity.value, actual = exc.httpStatusCode)
    }

    private fun createMockClient(
        handler: MockRequestHandler
    ): HttpClient {
        val json = Json.Default
        return HttpClient(MockEngine) {
            engine {
                addHandler(handler)
            }

            install(ExceptionPlugin) {
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
