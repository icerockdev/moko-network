package dev.icerock.moko.network

import dev.icerock.moko.network.features.LanguageFeature
import java.util.Locale

actual class LanguageProvider : LanguageFeature.LanguageCodeProvider {
    override fun getLanguageCode(): String? {
        return Locale.getDefault().displayLanguage
    }
}
