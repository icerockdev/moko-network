/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import platform.Foundation.NSError
import kotlin.native.concurrent.AtomicReference

object ThrowableToNSErrorMapper : (Throwable) -> NSError? {
    private val mapperRef: AtomicReference<((Throwable) -> NSError?)?> = AtomicReference(null)

    override fun invoke(throwable: Throwable): NSError? {
        return requireNotNull(mapperRef.value) { "please setup ThrowableToNSErrorMapper by call ThrowableToNSErrorMapper.setup() in iosMain or use dev.icerock.moko.network.createHttpClientEngine" }
            .invoke(throwable)
    }

    fun setup(block: (Throwable) -> NSError?) {
        mapperRef.value = block
    }
}
