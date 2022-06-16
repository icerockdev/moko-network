/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.errors.MR
import dev.icerock.moko.errors.handler.ExceptionHandler
import dev.icerock.moko.errors.mappers.ExceptionMappersStorage
import dev.icerock.moko.errors.presenters.AlertErrorPresenter
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.network.LanguageProvider
import dev.icerock.moko.network.plugins.LanguagePlugin
import dev.icerock.moko.network.plugins.TokenPlugin
import dev.icerock.moko.network.generated.apis.PetApi
import dev.icerock.moko.resources.desc.desc
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import news.apis.NewsApi

class TestViewModel : ViewModel() {

    val exceptionHandler = ExceptionHandler(
        errorPresenter = AlertErrorPresenter(
            // temporary fix https://youtrack.jetbrains.com/issue/KT-41823
            alertTitle = MR.strings.moko_errors_presenters_alertDialogTitle.desc(),
            positiveButtonText = MR.strings.moko_errors_presenters_alertPositiveButton.desc()
        ),
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
                override fun getToken(): String? = "ed155d0a445e4b4fbd878fe1f3bc1b7f"
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

    init {
        reloadPet()
        loadNews()
    }

    fun onRefreshPressed() {
        reloadPet()
    }

    private fun reloadPet() {
        viewModelScope.launch {
            exceptionHandler.handle {
                val pet = petApi.findPetsByStatus(listOf("available"))
                _petInfo.value = pet.toString()
            }.execute()
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
