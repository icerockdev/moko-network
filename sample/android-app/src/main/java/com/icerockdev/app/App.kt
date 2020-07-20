/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.app

import android.app.Application
import com.icerockdev.library.initExceptionStorage

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initExceptionStorage()
    }
}
