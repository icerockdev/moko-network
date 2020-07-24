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

class ErrorExceptionParser(private val json: Json) : HttpExceptionFactory.HttpExceptionParser {

    override fun parseException(
        request: HttpRequest,
        response: HttpResponse,
        responseBody: String?
    ): ResponseException? {
        try {
            val body = responseBody.orEmpty()
            val jsonRoot = json.parseJson(body)
            var jsonObject = jsonRoot.jsonObject
            if (jsonObject.containsKey(JSON_ERROR_KEY)) {
                jsonObject = jsonObject.getObject(JSON_ERROR_KEY)
            }

            var message: String? = null
            var code = response.status.value

            if (jsonObject.containsKey(JSON_MESSAGE_KEY)) {
                message = jsonObject.getPrimitiveOrNull(JSON_MESSAGE_KEY)?.contentOrNull
            }
            if (jsonObject.containsKey(JSON_CODE_KEY)) {
                val newCode = jsonObject.getPrimitiveOrNull(JSON_CODE_KEY)?.intOrNull
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