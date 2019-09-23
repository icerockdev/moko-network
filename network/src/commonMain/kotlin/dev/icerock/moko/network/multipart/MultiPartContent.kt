/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.multipart

import dev.icerock.moko.network.plus
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import io.ktor.util.flattenEntries
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.writeFully
import kotlinx.coroutines.io.writeStringUtf8
import kotlinx.io.charsets.Charsets
import kotlin.random.Random

// based on https://github.com/ktorio/ktor-samples/blob/master/other/client-multipart/src/MultipartApp.kt
data class MultiPartContent(val parts: List<Part>) : OutgoingContent.WriteChannelContent() {
    private val boundary = buildString {
        repeat(32) {
            append(Random.nextInt().toString(16))
        }
    }.take(70)

    data class Part(
        val name: String,
        val filename: String? = null,
        val headers: Headers = Headers.Empty,
        val writer: suspend ByteWriteChannel.() -> Unit
    )

    override suspend fun writeTo(channel: ByteWriteChannel) {
        for (part in parts) {
            channel.writeStringUtf8("--$boundary\r\n")
            val partHeaders = Headers.build {
                val fileNamePart =
                    if (part.filename != null) "; filename=\"${part.filename}\"" else ""
                append("Content-Disposition", "form-data; name=\"${part.name}\"$fileNamePart")
                appendAll(part.headers)
            }
            for ((key, value) in partHeaders.flattenEntries()) {
                channel.writeStringUtf8("$key: $value\r\n")
            }
            channel.writeStringUtf8("\r\n")
            part.writer(channel)
            channel.writeStringUtf8("\r\n")
        }
        channel.writeStringUtf8("--$boundary--\r\n")
    }

    override val contentType = ContentType.MultiPart.FormData
        .withParameter("boundary", boundary)
        .withCharset(Charsets.UTF_8)

    class Builder {
        private val parts = arrayListOf<Part>()

        fun add(part: Part) {
            parts += part
        }

        fun add(
            name: String,
            filename: String? = null,
            contentType: ContentType? = null,
            headers: Headers = Headers.Empty,
            writer: suspend ByteWriteChannel.() -> Unit
        ) {
            val contentTypeHeaders: Headers = if (contentType != null) headersOf(
                HttpHeaders.ContentType,
                contentType.toString()
            ) else headersOf()
            add(Part(name, filename, headers + contentTypeHeaders, writer))
        }

        fun add(
            name: String,
            text: String,
            contentType: ContentType? = null,
            filename: String? = null
        ) {
            add(name, filename, contentType) { writeStringUtf8(text) }
        }

        fun add(
            name: String,
            data: ByteArray,
            contentType: ContentType? = ContentType.Application.OctetStream,
            filename: String? = null
        ) {
            add(name, filename, contentType) { writeFully(data) }
        }

        internal fun build(): MultiPartContent = MultiPartContent(parts.toList())
    }

    companion object {
        fun build(callback: Builder.() -> Unit) = Builder().apply(callback).build()
    }
}