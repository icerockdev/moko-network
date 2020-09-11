/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptionfactory.parser

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ResponseException
import dev.icerock.moko.network.exceptions.ValidationException
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ValidationExceptionParser(private val json: Json) : HttpExceptionFactory.HttpExceptionParser {

    @Suppress("ReturnCount", "NestedBlockDepth")
    override fun parseException(
        request: HttpRequest,
        response: HttpResponse,
        responseBody: String?
    ): ResponseException? {
        @Suppress("TooGenericExceptionCaught")
        try {
            val body = responseBody.orEmpty()
            val jsonRoot = json.parseToJsonElement(body)
            try {
                val error = jsonRoot.jsonObject.getValue(JSON_ERROR_KEY).jsonObject

                return ErrorException(
                    request = request,
                    response = response,
                    code = error.getValue(JSON_CODE_KEY).jsonPrimitive.int,
                    description = error.getValue(JSON_MESSAGE_KEY).jsonPrimitive.content
                )
            } catch (e: NoSuchElementException) {
                val errorsJson = jsonRoot.jsonArray

                val errors = ArrayList<ValidationException.Error>(errorsJson.size)

                @Suppress("LoopWithTooManyJumpStatements")
                for (i in (0..errors.size)) {
                    try {
                        val jsonObject = errorsJson[i].jsonObject

                        val message: String
                        val field: String

                        if (jsonObject.containsKey(JSON_MESSAGE_KEY)) {
                            message = jsonObject.getValue(JSON_MESSAGE_KEY).jsonPrimitive.content
                        } else {
                            continue
                        }
                        if (jsonObject.containsKey(JSON_FIELD_KEY)) {
                            field = jsonObject.getValue(JSON_FIELD_KEY).jsonPrimitive.content
                        } else {
                            continue
                        }

                        errors.add(ValidationException.Error(field, message))
                    } catch (e: Exception) {
                        // ignore item
                    }
                }

                return ValidationException(request, response, responseBody.orEmpty(), errors)
            }
        } catch (e: Exception) {
            return null
        }
    }

    companion object {
        private const val JSON_MESSAGE_KEY = "message"
        private const val JSON_FIELD_KEY = "field"
        private const val JSON_ERROR_KEY = "error"
        private const val JSON_CODE_KEY = "code"
    }
}
