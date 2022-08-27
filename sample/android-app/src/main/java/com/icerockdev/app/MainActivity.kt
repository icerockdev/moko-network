/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.icerockdev.app.databinding.ActivityMainBinding
import com.icerockdev.library.TestViewModel
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory

class MainActivity : MvvmActivity<ActivityMainBinding, TestViewModel>() {
    override val layoutId: Int = R.layout.activity_main
    override val viewModelClass: Class<TestViewModel> = TestViewModel::class.java
    override val viewModelVariableId: Int = BR.viewModel

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory { TestViewModel() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.exceptionHandler.bind(this, this)

        val restText: TextView = findViewById(R.id.restText)
        val websocketText: TextView = findViewById(R.id.websocketText)
        val petsRefreshButton: Button = findViewById(R.id.refreshButton)
        val websocketRefreshButton: Button = findViewById(R.id.websocketButton)

        viewModel.petInfo.ld().observe(this) { data ->
            restText.text = data
        }
        viewModel.websocketInfo.ld().observe(this) { data ->
            websocketText.text = data
        }

        petsRefreshButton.setOnClickListener {
            viewModel.onRefreshPetPressed()
        }

        websocketRefreshButton.setOnClickListener {
            viewModel.onRefreshWebsocketPressed()
        }
    }
}
