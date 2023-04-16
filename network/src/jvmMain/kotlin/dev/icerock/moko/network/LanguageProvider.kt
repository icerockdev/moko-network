/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.plugins.LanguagePlugin
import java.util.Locale

actual class LanguageProvider : LanguagePlugin.LanguageCodeProvider {
    override fun getLanguageCode(): String? {
        return Locale.getDefault().displayLanguage
    }
}
