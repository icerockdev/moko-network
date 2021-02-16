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
import dev.icerock.moko.network.features.LanguageFeature
import dev.icerock.moko.network.features.TokenFeature
import dev.icerock.moko.network.generated.apis.PetApi
import dev.icerock.moko.resources.desc.desc
import io.ktor.client.HttpClient
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import news.apis.NewsApi

class TestViewModel : ViewModel() {
    private val customScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    val exceptionHandler = ExceptionHandler(
        errorPresenter = AlertErrorPresenter(
            // temporary fix https://youtrack.jetbrains.com/issue/KT-41823
            alertTitle = MR.strings.moko_errors_presenters_alertDialogTitle.desc(),
            positiveButtonText = MR.strings.moko_errors_presenters_alertPositiveButton.desc()
        ),
        exceptionMapper = ExceptionMappersStorage.throwableMapper()
    )

    private val httpClient = HttpClient {
        install(LanguageFeature) {
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

        install(TokenFeature) {
            tokenHeaderName = "Authorization"
            tokenProvider = object : TokenFeature.TokenProvider {
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
//        reloadPet()
//        loadNews()
    }

    override fun onCleared() {
        super.onCleared()

        customScope.cancel()

        httpClient.close()
    }

    fun onRefreshPressed() {
        reloadPet()
    }

    fun onSimpleRequestPressed() {
//        viewModelScope.launch {
//            val resp: String = httpClient.get {
//                url("https://petstore.swagger.io/v2/pet/findByStatus?status=available")
//            }
//            println(resp)
//        }
    }

    fun onSimpleRequestWithCustomScopePressed() {
//        customScope.launch {
//            val resp: String = httpClient.get {
//                url("https://petstore.swagger.io/v2/pet/findByStatus?status=available")
//            }
//            println(resp)
//        }
    }

    private fun reloadPet() {
//        viewModelScope.launch {
//            exceptionHandler.handle {
//                val pet = petApi.findPetsByStatus(listOf("available"))
//                _petInfo.value = pet.toString()
//            }.execute()
//        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadNews() {
//        viewModelScope.launch {
//            try {
//                val response = newApi.topHeadlinesGet(
//                    country = "ru",
//                    category = null,
//                    q = null,
//                    pageSize = null,
//                    page = null
//                )
//                println(response)
//            } catch (exception: Throwable) {
//                println("error to get news $exception")
//            }
//        }
    }
}
