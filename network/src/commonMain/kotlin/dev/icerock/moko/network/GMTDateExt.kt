/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.util.date.GMTDate

/**
 * Parses string from the beginning of the given string to produce a date by the [format].
 *
 * @throws IllegalArgumentException if the date [format] is incorrect.
 */
expect fun String.toDate(format: String): GMTDate

expect fun GMTDate.toString(format: String): String
