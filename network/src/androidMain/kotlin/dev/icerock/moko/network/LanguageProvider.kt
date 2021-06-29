/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import dev.icerock.moko.network.features.LanguageFeature

actual class LanguageProvider : LanguageFeature.LanguageCodeProvider {
    override fun getLanguageCode(): String? {
        return ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0).language
    }
}
