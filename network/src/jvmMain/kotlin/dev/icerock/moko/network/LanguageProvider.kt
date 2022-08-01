package dev.icerock.moko.network

import dev.icerock.moko.network.plugins.LanguagePlugin
import java.util.Locale

actual class LanguageProvider : LanguagePlugin.LanguageCodeProvider {
    override fun getLanguageCode(): String? {
        return Locale.getDefault().displayLanguage
    }
}
