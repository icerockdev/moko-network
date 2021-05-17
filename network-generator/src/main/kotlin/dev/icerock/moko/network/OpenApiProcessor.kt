/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse

internal class OpenApiProcessor {

    private val schemaProcessors = mutableSetOf<OpenApiSchemaProcessor>()

    fun addProcessor(processor: OpenApiSchemaProcessor) {
        schemaProcessors.add(processor)
    }

    fun process(openAPI: OpenAPI) {
        openAPI.paths.forEach { pathName, pathItem ->
            pathItem.processSchema(openAPI, pathName)
        }

        openAPI.components?.run {
            this.schemas?.keys?.toList()?.also { keys ->
                for (i in keys.indices) {
                    val name = keys[i]
                    val componentSchema = this.schemas?.get(name)
                    val context = SchemaContext.SchemaComponent(
                        schemaName = name
                    )
                    this.schemas[name] = componentSchema?.processSchema(openAPI, context)
                }
            }
            this.parameters?.forEach { (paramName, param) ->
                val context = SchemaContext.ParameterComponent(
                    parameterName = paramName,
                    parameter = param
                )
                param.schema = param.schema?.processSchema(openAPI, context)
            }
            this.requestBodies?.forEach { (requestName, requestBody) ->
                requestBody.processSchema(openAPI, requestName)
            }
            this.responses?.forEach { (responseName, response) ->
                response.processSchema(
                    openAPI,
                    responseName = responseName
                )
            }
        }
    }

    private fun PathItem.processSchema(openAPI: OpenAPI, pathName: String) {
        readOperationsMap().forEach { (method, operation) ->
            operation.processSchema(openAPI, pathName, this, method)
        }
    }

    private fun Operation.processSchema(
        openAPI: OpenAPI,
        pathName: String,
        pathItem: PathItem,
        method: PathItem.HttpMethod
    ) {
        requestBody?.processSchema(openAPI, pathName, pathItem, method, this)
        responses?.forEach { (responseName, apiResponse) ->
            apiResponse.processSchema(openAPI, pathName, pathItem, method, this, responseName)
        }
    }

    private fun RequestBody.processSchema(
        openAPI: OpenAPI,
        pathName: String,
        pathItem: PathItem,
        method: PathItem.HttpMethod,
        operation: Operation
    ) {
        content?.forEach { (contentName, content) ->
            val context = SchemaContext.OperationRequest(
                pathName = pathName,
                pathItem = pathItem,
                operation = operation,
                method = method,
                requestBody = this,
                contentName = contentName,
                mediaType = content
            )
            content.schema = content.schema?.processSchema(openAPI, context)
        }
    }

    private fun RequestBody.processSchema(
        openAPI: OpenAPI,
        requestName: String
    ) {
        content?.forEach { (contentName, content) ->
            val context = SchemaContext.Request(
                requestName = requestName,
                requestBody = this,
                contentName = contentName,
                mediaType = content
            )
            content.schema = content.schema?.processSchema(openAPI, context)
        }
    }

    @Suppress("LongParameterList")
    private fun ApiResponse.processSchema(
        openAPI: OpenAPI,
        pathName: String,
        pathItem: PathItem,
        method: PathItem.HttpMethod,
        operation: Operation,
        responseName: String
    ) {
        content?.forEach { (contentName, mediaType) ->
            val context = SchemaContext.OperationResponse(
                pathName = pathName,
                pathItem = pathItem,
                operation = operation,
                method = method,
                responseName = responseName,
                response = this,
                contentName = contentName,
                mediaType = mediaType
            )
            mediaType.schema = mediaType.schema?.processSchema(openAPI, context)
        }
    }

    private fun ApiResponse.processSchema(
        openAPI: OpenAPI,
        responseName: String
    ) {
        content?.forEach { (contentName, mediaType) ->
            val context = SchemaContext.Response(
                responseName = responseName,
                response = this,
                contentName = contentName,
                mediaType = mediaType
            )
            mediaType.schema = mediaType.schema?.processSchema(openAPI, context)
        }
    }

    private fun Schema<*>.processSchema(openAPI: OpenAPI, context: SchemaContext): Schema<*> {
        this.properties?.keys?.toList()?.also { keys ->
            for (i in keys.indices) {
                val propertyName = keys[i]
                val componentSchema = this.properties?.get(propertyName)
                val propertyContext = SchemaContext.PropertyComponent(
                    schemaName = this.name,
                    propertyName = propertyName
                )
                this.properties[propertyName] = componentSchema?.processSchema(
                    openAPI,
                    SchemaContext.Child(parent = context, child = propertyContext)
                )
            }
        }

        if (this is ArraySchema) {
            val propertyContext = SchemaContext.PropertyComponent(
                schemaName = this.name,
                propertyName = "items"
            )
            this.items = this.items.processSchema(
                openAPI,
                SchemaContext.Child(parent = context, child = propertyContext)
            )
        }

        if (additionalProperties != null && additionalProperties is Schema<*>) {
            val propertyContext = SchemaContext.PropertyComponent(
                schemaName = this.name,
                propertyName = "additionalProperties"
            )

            this.additionalProperties = (this.additionalProperties as Schema<*>).processSchema(
                openAPI,
                SchemaContext.Child(parent = context, child = propertyContext)
            )
        }

        return processSchemaByProcessors(
            input = this,
            openAPI = openAPI,
            context = context
        )
    }

    private fun processSchemaByProcessors(
        input: Schema<*>,
        openAPI: OpenAPI,
        context: SchemaContext
    ): Schema<*> {
        var result = input
        schemaProcessors.forEach {
            result = it.process(openAPI, result, context)
        }
        return result
    }
}
