/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.utils.io.errors.IOException

class ReceiveMessageException(message: String) : IOException(message)
