/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema

/**
 * OpenApi schema processor that removes all [null] items from enum fields of the schema.
 */
internal class SchemaEnumNullProcessor : OpenApiSchemaProcessor {
    override fun process(openApi: OpenAPI, schema: Schema<*>, context: SchemaContext): Schema<*> {
        val schemaProperties = schema.properties ?: return schema

        schemaProperties.forEach { (_, propSchema) ->
            val enumField = propSchema.enum
            if (enumField != null && enumField.isNotEmpty()) {
                propSchema.enum = enumField.filterNotNull()
            }
        }

        return schema
    }
}
