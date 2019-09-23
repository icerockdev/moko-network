/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptionfactory.parser

import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptions.ErrorException
import dev.icerock.moko.network.exceptions.ResponseException
import dev.icerock.moko.network.exceptions.ValidationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonException

class ValidationExceptionParser(private val json: Json) : HttpExceptionFactory.HttpExceptionParser {
    override fun parseException(httpCode: Int, responseBody: String?): ResponseException? {
        try {
            val body = responseBody.orEmpty()
            val jsonRoot = json.parseJson(body)
            try {
                val error = jsonRoot.jsonObject.getObject(JSON_ERROR_KEY)

                return ErrorException(
                    httpCode,
                    error.getPrimitive(JSON_CODE_KEY).int,
                    error.getPrimitive(JSON_MESSAGE_KEY).content
                )
            } catch (e: JsonException) {
                val errorsJson = jsonRoot.jsonArray

                val errors = ArrayList<ValidationException.Error>(errorsJson.size)

                for (i in (0..errors.size)) {
                    try {
                        val jsonObject = errorsJson.getObject(i)

                        val message: String
                        val field: String

                        if (jsonObject.containsKey(JSON_MESSAGE_KEY)) {
                            message = jsonObject.getPrimitive(JSON_MESSAGE_KEY).content
                        } else {
                            continue
                        }
                        if (jsonObject.containsKey(JSON_FIELD_KEY)) {
                            field = jsonObject.getPrimitive(JSON_FIELD_KEY).content
                        } else {
                            continue
                        }

                        errors.add(ValidationException.Error(field, message))
                    } catch (e: Exception) {
                        // ignore item
                    }
                }

                return ValidationException(httpCode, responseBody.orEmpty(), errors)
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