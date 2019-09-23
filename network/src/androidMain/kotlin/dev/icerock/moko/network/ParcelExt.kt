/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import android.os.Parcel
import io.ktor.util.date.GMTDate

fun Parcel.writeGMTDate(date: GMTDate) {
    writeLong(date.timestamp)
}

fun Parcel.readGMTDate(): GMTDate {
    return GMTDate(timestamp = readLong())
}

fun Parcel.writeGMTDateSafe(value: GMTDate?) {
    if (value == null) {
        writeByte(0)
    } else {
        writeByte(1)
        writeGMTDate(value)
    }
}

fun Parcel.readGMTDateSafe(): GMTDate? {
    return if (readByte() == 1.toByte()) {
        readGMTDate()
    } else {
        null
    }
}