/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

open class ResponseException(
    val httpStatusCode: Int,
    responseMessage: String
) : Exception(responseMessage) {

    val isUnauthorized: Boolean
        get() = httpStatusCode == 401

    val isAccessDenied: Boolean
        get() = httpStatusCode == 403

    val isNotFound: Boolean
        get() = httpStatusCode == 404
}
