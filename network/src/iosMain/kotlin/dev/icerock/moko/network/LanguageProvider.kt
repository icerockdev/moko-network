/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.plugins.LanguagePlugin
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual class LanguageProvider : LanguagePlugin.LanguageCodeProvider {
    override fun getLanguageCode(): String {
        return NSLocale.currentLocale.languageCode
    }
}
