/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.errors.handler.ExceptionHandler
import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.errors.presenters.AlertErrorPresenter
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.network.LanguageProvider
import dev.icerock.moko.network.createHttpClientEngine
import dev.icerock.moko.network.generated.apis.PetApi
import dev.icerock.moko.network.plugins.LanguagePlugin
import dev.icerock.moko.network.plugins.TokenPlugin
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import news.apis.NewsApi

class TestViewModel : ViewModel() {

    val exceptionHandler = ExceptionHandler(
        errorPresenter = AlertErrorPresenter(),
        exceptionMapper = ExceptionMappersStorage.throwableMapper(),
        onCatch = { it.printStackTrace() }
    )

    private val httpClient = HttpClient {
        install(LanguagePlugin) {
            languageHeaderName = "X-Language"
            languageCodeProvider = LanguageProvider()
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }

        install(TokenPlugin) {
            tokenHeaderName = "Authorization"
            tokenProvider = object : TokenPlugin.TokenProvider {
                override fun getToken(): String = "ed155d0a445e4b4fbd878fe1f3bc1b7f"
            }
        }
    }
    private val petApi = PetApi(
        basePath = "https://petstore.swagger.io/v2/",
        httpClient = httpClient,
        json = Json {
            ignoreUnknownKeys = true
        }
    )

    private val newApi = NewsApi(
        basePath = "https://newsapi.org/v2/",
        httpClient = httpClient,
        json = Json {
            ignoreUnknownKeys = true
        }
    )

    private val _petInfo = MutableLiveData<String?>(null)
    val petInfo: LiveData<String?> = _petInfo.readOnly()

    private val _websocketInfo = MutableLiveData<String?>("")
    val websocketInfo: LiveData<String?> = _websocketInfo.readOnly()

    init {
        reloadPet()
        loadNews()
    }

    fun onRefreshPetPressed() {
        reloadPet()
    }

    fun onRefreshWebsocketPressed() {
        reloadWebsocket()
    }

    private fun reloadPet() {
        viewModelScope.launch {
            exceptionHandler.handle {
                val pet = petApi.findPetsByStatus(listOf("available"))
                _petInfo.value = pet.toString()
            }.execute()
        }
    }

    private fun reloadWebsocket() {

        val httpClient = HttpClient(createHttpClientEngine()) {
            install(WebSockets)
        }
        viewModelScope.launch {
            _websocketInfo.value += "try connect websocket\n"
            httpClient.webSocket("ws://$emulatorLocalhost:8080/myws/echo") {
                _websocketInfo.value += "connected websocket\n"

                val incomingJob = viewModelScope.launch {
                    incoming.consumeEach { frame ->
                        println(frame.toString())

                        if (frame is Frame.Text) {
                            val text: String = frame.readText()
                            _websocketInfo.value += "received $text\n"

                            outgoing.send(Frame.Text(">$text"))
                            _websocketInfo.value += "send response\n"
                        }
                    }
                }
                send(Frame.Text("Hello world!"))
                _websocketInfo.value += "send first message\n"

                incomingJob.join()
                _websocketInfo.value += "incoming job end\n"
            }

            _websocketInfo.value += "websocket closed\n"
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadNews() {
        viewModelScope.launch {
            try {
                val response = newApi.topHeadlinesGet(
                    country = "ru",
                    category = null,
                    q = null,
                    pageSize = null,
                    page = null
                )
                println(response)
            } catch (exception: Throwable) {
                println("error to get news $exception")
            }
        }
    }
}
