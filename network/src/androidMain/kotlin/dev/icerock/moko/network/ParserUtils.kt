/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.util.date.GMTDate
import java.text.SimpleDateFormat
import java.util.*

actual fun String.formatToDate(parseFormat: String): GMTDate {
    val date = SimpleDateFormat(parseFormat, Locale.ROOT).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }.parse(this)
    return GMTDate(date.time)
}