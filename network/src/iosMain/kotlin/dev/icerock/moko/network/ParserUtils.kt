/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.util.date.GMTDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970

actual fun String.formatToDate(parseFormat: String): GMTDate {
    val formatter = NSDateFormatter()
    formatter.dateFormat = parseFormat
    return GMTDate(
        formatter.dateFromString(this)?.timeIntervalSince1970?.toLong()
            ?: throw IllegalArgumentException("Failed, to parse $this for format $parseFormat")
    )
}
