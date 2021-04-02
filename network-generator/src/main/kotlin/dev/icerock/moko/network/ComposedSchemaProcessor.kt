/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema

internal class ComposedSchemaProcessor(
    private val operationIdGenerator: (operation: Operation, path: String, method: String) -> String
) : OpenApiSchemaProcessor {

    @Suppress("ReturnCount")
    override fun process(openApi: OpenAPI, schema: Schema<*>, context: SchemaContext): Schema<*> {
        if (schema !is ComposedSchema) return schema

        if (schema.allOf != null) {
            return processAllOfSchema(
                schemas = openApi.components.schemas,
                schema = schema,
                context = context,
                allOfSchemas = schema.allOf
            )
        }
        if (schema.anyOf != null) {
            return processAnyOfSchema(
                schemas = openApi.components.schemas,
                schema = schema,
                context = context,
                anyOfSchemas = schema.anyOf
            )
        }

        return schema
    }

    private fun processAllOfSchema(
        schemas: MutableMap<String, Schema<*>>,
        schema: ComposedSchema,
        context: SchemaContext,
        allOfSchemas: List<Schema<*>>
    ): Schema<*> {
        if (allOfSchemas.size == 1) return allOfSchemas.first().withPropsOf(schema)

        val newSchemaName: String = context.buildSchemaName() + "_composed"
        extractSchema(
            schemas = schemas,
            newSchemaName = newSchemaName,
            markExtension = "x-allOfGeneration",
            propertySchemas = allOfSchemas
        )

        return Schema<Any>().apply {
            `$ref` = "#/components/schemas/$newSchemaName"
        }
    }

    private fun processAnyOfSchema(
        schemas: MutableMap<String, Schema<*>>,
        schema: ComposedSchema,
        context: SchemaContext,
        anyOfSchemas: List<Schema<*>>
    ): Schema<*> {
        if (anyOfSchemas.size == 1) return anyOfSchemas.first().withPropsOf(schema)

        val newSchemaName: String = context.buildSchemaName() + "_composed"
        extractSchema(
            schemas = schemas,
            newSchemaName = newSchemaName,
            markExtension = "x-anyOfGeneration",
            propertySchemas = anyOfSchemas
        )

        return Schema<Any>().apply {
            `$ref` = "#/components/schemas/$newSchemaName"
        }
    }

    private fun extractSchema(
        schemas: MutableMap<String, Schema<*>>,
        newSchemaName: String,
        markExtension: String,
        propertySchemas: List<Schema<*>>
    ) {
        val resultSchema = Schema<Any>().apply {
            addExtension(markExtension, true)
            properties = mutableMapOf()
        }
        schemas[newSchemaName] = resultSchema

        var inlineIdx = 1
        propertySchemas.forEachIndexed { index, schema ->
            val propertyName = "item_$index"

            if (schema.`$ref` == null) {
                val name = newSchemaName + "_inline_" + inlineIdx
                inlineIdx++
                if (schemas.containsKey(name)) throw IllegalAccessException(name)

                schemas[name] = schema

                resultSchema.properties[propertyName] = Schema<Any>().apply {
                    `$ref` = "#/components/schemas/$name"
                }
            } else {
                resultSchema.properties[propertyName] = schema
            }
        }
    }

    private fun Schema<*>.withPropsOf(schema: ComposedSchema) = apply {
        nullable = schema.nullable
    }

    private fun SchemaContext.buildSchemaName(): String {
        return when (this) {
            is SchemaContext.OperationResponse -> {
                operationIdGenerator(
                    operation,
                    pathName,
                    method.name.toLowerCase().capitalize()
                ) + "_response_" + this.responseName
            }
            is SchemaContext.Response -> this.responseName
            is SchemaContext.Request -> this.requestName
            is SchemaContext.OperationRequest -> operationIdGenerator(
                operation,
                pathName,
                method.name.toLowerCase().capitalize()
            ) + "_requestBody"
            is SchemaContext.ParameterComponent -> this.parameterName
            is SchemaContext.SchemaComponent -> this.schemaName
            is SchemaContext.PropertyComponent -> this.schemaName.orEmpty() + "_" + this.propertyName
            is SchemaContext.Child -> this.parent.buildSchemaName() + "_" + this.child.buildSchemaName()
        }
    }
}
