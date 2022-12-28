/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.server

import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        post("/v1/auth/signup") {
            var fileName = ""
            val map = mutableMapOf<String?, String>()
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        map[part.name] = part.value
                    }

                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        val dir = File("uploads")
                        dir.mkdir()
                        File(dir, "$fileName.jpg").writeBytes(fileBytes)
                    }

                    else -> {}
                }
                part.dispose()
            }

            val response = """
                    {
                        "status": 200,
                        "message": "avatar uploaded",
                        "timestamp": 123.0,
                        "success": true
                    }
                """.trimIndent()
            println("received map: $map and file uploaded to 'uploads/$fileName'")
            println(response)
            call.respondText(response)
        }
    }
}