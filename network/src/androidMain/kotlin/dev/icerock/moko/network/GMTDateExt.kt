/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.util.date.GMTDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun String.toDate(format: String) = try {
    GMTDate(SimpleDateFormat(format, Locale.getDefault()).parse(this).time)
} catch (parseException: ParseException) {
    throw IllegalArgumentException("Parsing error: the date format is incorrect")
}

actual fun GMTDate.toString(format: String): String =
    SimpleDateFormat(format, Locale.getDefault()).format(Date(timestamp))
