/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.servers.Server
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenType
import org.openapitools.codegen.languages.AbstractKotlinCodegen

class KtorCodegen : AbstractKotlinCodegen() {
    /**
     * Constructs an instance of `KtorCodegen`.
     */
    init {
        artifactId = "kotlin-ktor-client"
        packageName = "dev.icerock.moko.network.generated"

        nonPublicApi = true

        typeMapping["array"] = "kotlin.collections.List"
        typeMapping["number"] = "kotlin.Double"

        typeMapping["date"] = "kotlin.String"
        typeMapping["date-time"] = "kotlin.String"
        typeMapping["Date"] = "kotlin.String"
        typeMapping["DateTime"] = "kotlin.String"

        @Suppress("ForbiddenComment")
        // TODO: Support files via ByteArray / LargeTextContent
        typeMapping["File"] = "kotlin.String"
        typeMapping["file"] = "kotlin.String"

        typeMapping["UUID"] = "kotlin.String"

        embeddedTemplateDir = "kotlin-ktor-client"

        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["api.mustache"] = ".kt"
        modelDocTemplateFiles["model_doc.mustache"] = ".md"
        apiDocTemplateFiles["api_doc.mustache"] = ".md"
        apiPackage = "$packageName.apis"
        modelPackage = "$packageName.models"
    }

    override fun getTag(): CodegenType {
        return CodegenType.CLIENT
    }

    override fun getName(): String {
        return "kotlin-ktor-client"
    }

    override fun getHelp(): String {
        return "Generates a kotlin ktor client."
    }

    override fun fromOperation(
        path: String,
        httpMethod: String,
        operation: io.swagger.v3.oas.models.Operation?,
        servers: List<Server>?
    ): CodegenOperation {
        val codegenOperation = super.fromOperation(path, httpMethod, operation, servers)
        codegenOperation.httpMethod = codegenOperation.httpMethod.firstCapitalized()
        var currentPath = codegenOperation.path
        for (param in codegenOperation.pathParams) {
            currentPath = currentPath.replace("{" + param.baseName + "}", "$" + param.paramName)
        }
        if (currentPath[0] == '/') {
            currentPath = currentPath.substring(1)
        }
        codegenOperation.path = currentPath
        if (codegenOperation.returnType != null && codegenOperation.returnType == "Any") {
            codegenOperation.returnType = null
        }
        return codegenOperation
    }

    private fun String.firstCapitalized(): String {
        return substring(0, 1).toUpperCase() + substring(1).toLowerCase()
    }

    override fun getEnumPropertyNaming(): CodegenConstants.ENUM_PROPERTY_NAMING_TYPE {
        return CodegenConstants.ENUM_PROPERTY_NAMING_TYPE.UPPERCASE
    }
}
