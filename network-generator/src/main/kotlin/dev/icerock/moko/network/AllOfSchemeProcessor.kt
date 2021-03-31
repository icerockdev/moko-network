/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse

internal class AllOfSchemeProcessor : OpenApiSchemaProcessor {

    override fun process(openApi: OpenAPI, schema: Schema<*>) {
        openApi.paths?.forEach { _, item ->
            val operations: List<Operation> = item.readOperations()
            operations.forEach { operation ->
                operation.responses.forEach { name, responseBody ->
                    processResponse(
                        responseBody,
                        responseName = operation.operationId + "_response_" + name,
                        openApi.components.schemas
                    )
                }
            }
        }

        openApi.components?.responses?.forEach { (responseName, responseBody) ->
            processResponse(responseBody, responseName, openApi.components.schemas)
        }
    }

    @Suppress("ReturnCount")
    private fun processResponse(
        responseBody: ApiResponse?,
        responseName: String?,
        schemas: MutableMap<String, Schema<*>>
    ) {
        val jsonContent: MediaType = responseBody?.content?.get("application/json") ?: return
        val jsonSchema: ComposedSchema = jsonContent.schema as? ComposedSchema ?: return
        val allOfSchemas: List<Schema<*>> = jsonSchema.allOf ?: return

        val allOfSchemaName: String = responseName ?: jsonContent.toString()
        extractAllOfSchema(schemas, allOfSchemaName, allOfSchemas)

        jsonContent.schema = Schema<Any>().apply {
            `$ref` = "#/components/schemas/$allOfSchemaName"
        }
    }

    private fun extractAllOfSchema(
        schemas: MutableMap<String, Schema<*>>,
        allOfSchemaName: String,
        allOfSchemas: List<Schema<*>>
    ) {
        val allOfSchema = Schema<Any>().apply {
            // mark our synthetic schema by this name, to mark codegen model later for correct template processing
            name = "allOf"
            properties = mutableMapOf()
        }
        schemas[allOfSchemaName] = allOfSchema

        var inlineIdx = 1
        allOfSchemas.forEachIndexed { index, schema ->
            val propertyName = "item_$index"

            if (schema.`$ref` == null) {
                val name = allOfSchemaName + "_inline_" + inlineIdx
                inlineIdx++
                if (schemas.containsKey(name)) throw IllegalAccessException(name)

                schemas[name] = schema

                allOfSchema.properties[propertyName] = Schema<Any>().apply {
                    `$ref` = "#/components/schemas/$name"
                }
            } else {
                allOfSchema.properties[propertyName] = schema
            }
        }
    }
}
