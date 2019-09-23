/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.charset
import io.ktor.http.content.OutgoingContent
import kotlinx.io.charsets.Charset
import kotlinx.io.charsets.Charsets

/**
 * Свой вариант io.ktor.http.content.TextContent с кастомным преобразованием строки в набор байт, так
 * как стандартный вариант из ktor зависает на iOS при отправке большого контента (например фото в base64).
 * Удалить после закрытия https://github.com/Kotlin/kotlinx-io/issues/47
 */
class LargeTextContent(
    val text: String,
    override val contentType: ContentType,
    override val status: HttpStatusCode? = null
) : OutgoingContent.ByteArrayContent() {
    private val bytes by lazy(LazyThreadSafetyMode.NONE) {
        text.toByteArrayPlatform(contentType.charset() ?: Charsets.UTF_8)
    }

    override val contentLength: Long
        get() = bytes.size.toLong()

    override fun bytes(): ByteArray = bytes

    override fun toString(): String = "LargeTextContent[$contentType] \"${text.take(30)}\""
}

expect fun String.toByteArrayPlatform(charset: Charset): ByteArray
