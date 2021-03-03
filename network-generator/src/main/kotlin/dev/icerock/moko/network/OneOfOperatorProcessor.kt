/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse

internal class OneOfOperatorProcessor(private val propertyNewType: String) {

    fun replaceOneOfOperatorsToType(openAPI: OpenAPI) {
        openAPI.paths.forEach { pathName, pathItem ->
            pathItem.processOneOfOperator()
        }

        openAPI.components?.run {
            this.schemas?.forEach { (_, componentSchema) ->
                componentSchema.replaceOneOfOperator()
            }
            this.parameters?.forEach { (_, param) ->
                param.schema.replaceOneOfOperator()
            }
            this.requestBodies?.forEach { (_, requestBody) ->
                requestBody.content?.forEach { (_, content) ->
                    content.schema?.replaceOneOfOperator()
                }
            }
            this.responses?.forEach { (_, response) ->
                response.processOneOfOperator()
            }
        }
    }

    private fun PathItem.processOneOfOperator() {
        get?.processResponsesOneOfOperator()
        post?.processResponsesOneOfOperator()
        patch?.processResponsesOneOfOperator()
        delete?.processResponsesOneOfOperator()
        put?.processResponsesOneOfOperator()
        options?.processResponsesOneOfOperator()
        head?.processResponsesOneOfOperator()
        trace?.processResponsesOneOfOperator()
    }

    private fun Operation.processResponsesOneOfOperator() {
        responses?.forEach { (_, apiResponse) ->
            apiResponse.processOneOfOperator()
        }
    }

    private fun ApiResponse.processOneOfOperator() {
        content?.forEach { (_, mediaType) ->
            mediaType.schema.replaceOneOfOperator()
        }
    }

    private fun Schema<*>?.replaceOneOfOperator() {
        if (this == null) return
        val schemaProperties = properties ?: return

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
