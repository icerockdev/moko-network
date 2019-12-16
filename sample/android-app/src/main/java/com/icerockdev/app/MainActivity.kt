/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.icerockdev.library.TestViewModel
import dev.icerock.moko.mvvm.getViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val textView: TextView = findViewById(R.id.textView)
        val refreshButton: Button = findViewById(R.id.refreshButton)

        val viewModel = getViewModel { TestViewModel() }

        viewModel.petInfo.ld().observe(this, Observer { url ->
            textView.text = url
        })

        refreshButton.setOnClickListener {
            viewModel.onRefreshPressed()
        }
    }
}
