/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptionfactory.parser

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ResponseException
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ErrorExceptionParser(private val json: Json) : HttpExceptionFactory.HttpExceptionParser {

    override fun parseException(
        request: HttpRequest,
        response: HttpResponse,
        responseBody: String?
    ): ResponseException? {
        @Suppress("TooGenericExceptionCaught")
        try {
            val body = responseBody.orEmpty()
            val jsonRoot = json.parseToJsonElement(body)
            var jsonObject = jsonRoot.jsonObject
            if (jsonObject.containsKey(JSON_ERROR_KEY)) {
                jsonObject = jsonObject.getValue(JSON_ERROR_KEY).jsonObject
            }

            var message: String? = null
            var code = response.status.value

            if (jsonObject.containsKey(JSON_MESSAGE_KEY)) {
                message = jsonObject[JSON_MESSAGE_KEY]?.jsonPrimitive?.contentOrNull
            }
            if (jsonObject.containsKey(JSON_CODE_KEY)) {
                val newCode = jsonObject[JSON_CODE_KEY]?.jsonPrimitive?.intOrNull
                if (newCode != null) {
                    code = newCode
                }
            }
            return ErrorException(request, response, code, message)
        } catch (e: Exception) {
            return null
        }
    }

    companion object {
        private const val JSON_MESSAGE_KEY = "message"
        private const val JSON_CODE_KEY = "code"
        private const val JSON_ERROR_KEY = "error"
    }
}
