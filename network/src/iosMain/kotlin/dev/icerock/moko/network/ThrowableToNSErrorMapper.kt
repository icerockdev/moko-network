/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("MaximumLineLength")

package dev.icerock.moko.network

import platform.Foundation.NSError
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

object ThrowableToNSErrorMapper : (Throwable) -> NSError? {
    @OptIn(ExperimentalAtomicApi::class)
    private val mapperRef: AtomicReference<((Throwable) -> NSError?)?> = AtomicReference(null)

    @OptIn(ExperimentalAtomicApi::class)
    override fun invoke(throwable: Throwable): NSError? {
        @Suppress("MaxLineLength")
        return requireNotNull(mapperRef.load()) {
            "please setup ThrowableToNSErrorMapper by call ThrowableToNSErrorMapper.setup() in iosMain or use dev.icerock.moko.network.createHttpClientEngine"
        }.invoke(throwable)
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun setup(block: (Throwable) -> NSError?) {
        mapperRef.store(block)
    }
}
