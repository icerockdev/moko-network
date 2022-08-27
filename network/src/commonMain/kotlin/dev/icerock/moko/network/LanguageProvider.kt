/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import dev.icerock.moko.network.plugins.LanguagePlugin

@Suppress("EmptyDefaultConstructor")
expect class LanguageProvider() : LanguagePlugin.LanguageCodeProvider
