/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

class ValidationException(
    httpStatusCode: Int,
    message: String,
    val errors: List<Error>
) : ResponseException(httpStatusCode, message) {
    data class Error(val field: String, val message: String)
}