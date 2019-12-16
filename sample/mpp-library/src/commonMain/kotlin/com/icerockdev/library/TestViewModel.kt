/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.network.features.TokenFeature
import dev.icerock.moko.network.generated.apis.GifsApi
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class TestViewModel : ViewModel() {
    private val httpClient = HttpClient {
        install(TokenFeature) {
            tokenHeaderName = "api_key"
            tokenProvider = object : TokenFeature.TokenProvider {
                override fun getToken(): String? {
                    return "o5tAxORWRXRxxgIvRthxWnsjEbA3vkjV"
                }
            }
        }
    }
    private val gifsApi = GifsApi(
        basePath = "https://api.giphy.com/v1/",
        httpClient = httpClient,
        json = Json.nonstrict
    )

    private val _gifUrl = MutableLiveData<String?>(null)
    val gifUrl: LiveData<String?> = _gifUrl.readOnly()

    init {
        reloadGif()
    }

    fun onRefreshPressed() {
        reloadGif()
    }

    private fun reloadGif() {
        viewModelScope.launch {
            try {
                val gif = gifsApi.randomGif(
                    rating = null,
                    tag = null
                )

                _gifUrl.value = gif.data?.images?.original?.url

            } catch (error: Exception) {
                println("can't load $error")
            }
        }
    }
}
