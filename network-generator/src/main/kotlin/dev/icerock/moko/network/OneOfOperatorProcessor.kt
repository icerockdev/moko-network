/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema

/**
 * OpenApi schema processor that replace oneOf operator to the temporary type [propertyNewType].
 */
internal class OneOfOperatorProcessor(
    private val propertyNewType: String
) : OpenApiSchemaProcessor {

    override fun process(schema: Schema<*>) {
        val schemaProperties = schema.properties ?: return

        schemaProperties.forEach { (_, propSchema) ->
            if (propSchema == null ||
                propSchema !is ComposedSchema ||
                propSchema.oneOf?.isEmpty() == true
            ) {
                return@forEach
            }

            propSchema.type = propertyNewType
            propSchema.oneOf = null
            propSchema.`$ref` = null
        }
    }
}
