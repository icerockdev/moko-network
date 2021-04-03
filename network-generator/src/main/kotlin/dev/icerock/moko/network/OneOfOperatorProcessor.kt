/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema

/**
 * OpenApi schema processor that replace oneOf operator to the temporary type [propertyNewType].
 */
internal class OneOfOperatorProcessor(
    private val propertyNewType: String
) : OpenApiSchemaProcessor {

    @Suppress("ReturnCount")
    override fun process(openApi: OpenAPI, schema: Schema<*>, context: SchemaContext): Schema<*> {
        if (schema !is ComposedSchema) return schema
        if (schema.oneOf.isNullOrEmpty()) return schema

        return Schema<Any>().apply {
            type = propertyNewType
        }
    }
}
