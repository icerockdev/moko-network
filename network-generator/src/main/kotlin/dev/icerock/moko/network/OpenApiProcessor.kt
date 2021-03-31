/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse

internal class OpenApiProcessor {

    private val schemaProcessors = mutableSetOf<OpenApiSchemaProcessor>()

    fun addProcessor(processor: OpenApiSchemaProcessor) {
        schemaProcessors.add(processor)
    }

    fun process(openAPI: OpenAPI) {
        openAPI.paths.forEach { _, pathItem ->
            pathItem.processSchema(openAPI)
        }

        openAPI.components?.run {
            this.schemas?.forEach { (_, componentSchema) ->
                componentSchema?.processSchema(openAPI)
            }
            this.parameters?.forEach { (_, param) ->
                param.schema?.processSchema(openAPI)
            }
            this.requestBodies?.forEach { (_, requestBody) ->
                requestBody.content?.forEach { (_, content) ->
                    content.schema?.processSchema(openAPI)
                }
            }
            this.responses?.forEach { (_, response) ->
                response.processSchema(openAPI)
            }
        }
    }

    private fun PathItem.processSchema(openAPI: OpenAPI) {
        readOperations().forEach { it.processSchema(openAPI) }
    }

    private fun Operation.processSchema(openAPI: OpenAPI) {
        responses?.forEach { (_, apiResponse) ->
            apiResponse.processSchema(openAPI)
        }
    }

    private fun ApiResponse.processSchema(openAPI: OpenAPI) {
        content?.forEach { (_, mediaType) ->
            mediaType.schema?.processSchema(openAPI)
        }
    }

    private fun Schema<*>.processSchema(openAPI: OpenAPI) {
        schemaProcessors.forEach {
            it.process(openAPI, this)
        }
    }
}
