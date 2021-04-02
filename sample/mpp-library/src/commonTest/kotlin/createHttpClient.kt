/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptionfactory.parser.ErrorExceptionParser
import dev.icerock.moko.network.exceptionfactory.parser.ValidationExceptionParser
import dev.icerock.moko.network.features.ExceptionFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

fun createMockClient(
    json: Json = Json {
        ignoreUnknownKeys = true
    },
    handler: MockRequestHandler
): HttpClient {
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
