package dev.icerock.moko.network

import io.swagger.v3.oas.models.media.Schema

fun interface OpenApiSchemaProcessor {
    fun process(schema: Schema<*>)
}
