/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.app

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.icerockdev.library.TestViewModel
import dev.icerock.moko.mvvm.getViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val imageView: ImageView = findViewById(R.id.imageView)
        val refreshButton: Button = findViewById(R.id.refreshButton)
        val glide = Glide.with(this)

        val viewModel = getViewModel { TestViewModel() }

        viewModel.gifUrl.ld().observe(this, Observer { url ->
            glide.load(url)
                .placeholder(R.drawable.loading)
                .into(imageView)
        })

        refreshButton.setOnClickListener {
            viewModel.onRefreshPressed()
        }
    }
}
