/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.serializer

interface SerialEnum {
    val serialName: String?
}

fun <T> Array<T>.serial() where T : SerialEnum, T : Enum<T> =
    this.map { it.serialName ?: it.name }.toTypedArray()