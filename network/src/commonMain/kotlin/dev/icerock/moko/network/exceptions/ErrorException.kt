/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.exceptions

class ErrorException(
    httpStatusCode: Int,
    val code: Int,
    val description: String?
) : ResponseException(httpStatusCode, description.orEmpty()) {
    override val message: String?
        get() = description ?: super.message
}
