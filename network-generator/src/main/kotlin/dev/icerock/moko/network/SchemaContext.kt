/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse

sealed class SchemaContext {
    data class OperationResponse(
        val pathName: String,
        val pathItem: PathItem,
        val method: PathItem.HttpMethod,
        val operation: Operation,
        val responseName: String,
        val response: ApiResponse,
        val contentName: String,
        val mediaType: MediaType
    ) : SchemaContext()

    data class Response(
        val responseName: String,
        val response: ApiResponse,
        val contentName: String,
        val mediaType: MediaType
    ) : SchemaContext()

    data class OperationRequest(
        val pathName: String,
        val pathItem: PathItem,
        val method: PathItem.HttpMethod,
        val operation: Operation,
        val requestBody: RequestBody,
        val contentName: String,
        val mediaType: MediaType
    ) : SchemaContext()

    data class Request(
        val requestName: String,
        val requestBody: RequestBody,
        val contentName: String,
        val mediaType: MediaType
    ) : SchemaContext()

    data class ParameterComponent(
        val parameterName: String,
        val parameter: Parameter
    ) : SchemaContext()

    data class SchemaComponent(
        val schemaName: String
    ) : SchemaContext()

    data class PropertyComponent(
        val schemaName: String?,
        val propertyName: String
    ) : SchemaContext()

    data class Child(
        val parent: SchemaContext,
        val child: SchemaContext
    ) : SchemaContext()
}
