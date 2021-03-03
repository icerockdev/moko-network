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
        openAPI.paths.forEach { pathName, pathItem ->
            pathItem.processSchema()
        }

        openAPI.components?.run {
            this.schemas?.forEach { (_, componentSchema) ->
                componentSchema?.processSchema()
            }
            this.parameters?.forEach { (_, param) ->
                param.schema?.processSchema()
            }
            this.requestBodies?.forEach { (_, requestBody) ->
                requestBody.content?.forEach { (_, content) ->
                    content.schema?.processSchema()
                }
            }
            this.responses?.forEach { (_, response) ->
                response.processSchema()
            }
        }
    }

    private fun PathItem.processSchema() {
        get?.processSchema()
        post?.processSchema()
        patch?.processSchema()
        delete?.processSchema()
        put?.processSchema()
        options?.processSchema()
        head?.processSchema()
        trace?.processSchema()
    }

    private fun Operation.processSchema() {
        responses?.forEach { (_, apiResponse) ->
            apiResponse.processSchema()
        }
    }

    private fun ApiResponse.processSchema() {
        content?.forEach { (_, mediaType) ->
            mediaType.schema?.processSchema()
        }
    }

    private fun Schema<*>.processSchema() {
        schemaProcessors.forEach {
            it.process(this)
        }
    }
}
