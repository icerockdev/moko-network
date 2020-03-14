/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.util.date.GMTDate
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.timeIntervalSince1970


actual fun String.toDate(format: String): GMTDate {
    val formatter = NSDateFormatter()
    val locale = NSLocale.currentLocale()
    formatter.setDateFormat(format)
    formatter.setLocale(locale)
    val date: NSDate = formatter.dateFromString(this)!!
    val timestamp: Long = (date.timeIntervalSince1970 * 1000).toLong()
    return GMTDate(timestamp)
}

actual fun GMTDate.toString(format: String): String {
    val formatter = NSDateFormatter()
    val locale = NSLocale.currentLocale()
    formatter.setDateFormat(format)
    formatter.setLocale(locale)
    val timestamp =
        (this.timestamp.toDouble() / 1000) - (NSDate().timeIntervalSince1970 - NSDate().timeIntervalSinceReferenceDate)
    val date: NSDate = NSDate(timestamp)
    return formatter.stringFromDate(date)
}
