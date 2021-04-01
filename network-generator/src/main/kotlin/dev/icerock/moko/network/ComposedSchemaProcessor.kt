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
        val newSchemaName: String = context.buildSchemaName() + "_composed"
        extractAllOfSchema(schemas, newSchemaName, allOfSchemas)

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
        val newSchemaName: String = context.buildSchemaName() + "_composed"
        extractAnyOfSchema(schemas, newSchemaName, anyOfSchemas)

        return Schema<Any>().apply {
            `$ref` = "#/components/schemas/$newSchemaName"
        }
    }

    private fun extractAllOfSchema(
        schemas: MutableMap<String, Schema<*>>,
        allOfSchemaName: String,
        allOfSchemas: List<Schema<*>>
    ) {
        val allOfSchema = Schema<Any>().apply {
            // mark our synthetic schema by this name, to mark codegen model later for correct template processing
            addExtension("x-allOfGeneration", true)
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

    private fun extractAnyOfSchema(
        schemas: MutableMap<String, Schema<*>>,
        newSchemaName: String,
        anyOfSchemas: List<Schema<*>>
    ) {
        val anyOfSchema = Schema<Any>().apply {
            // mark our synthetic schema by this name, to mark codegen model later for correct template processing
            addExtension("x-anyOfGeneration", true)
            properties = mutableMapOf()
        }
        schemas[newSchemaName] = anyOfSchema

        var inlineIdx = 1
        anyOfSchemas.forEachIndexed { index, schema ->
            val propertyName = "item_$index"

            if (schema.`$ref` == null) {
                val name = newSchemaName + "_inline_" + inlineIdx
                inlineIdx++
                if (schemas.containsKey(name)) throw IllegalAccessException(name)

                schemas[name] = schema

                anyOfSchema.properties[propertyName] = Schema<Any>().apply {
                    `$ref` = "#/components/schemas/$name"
                }
            } else {
                anyOfSchema.properties[propertyName] = schema
            }
        }
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
            is SchemaContext.PropertyComponent -> this.schemaName + "_" + this.propertyName
            is SchemaContext.Child -> this.parent.buildSchemaName() + "_" + this.child.buildSchemaName()
        }
    }
}
