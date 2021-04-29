/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network.safeable

import kotlinx.serialization.Serializable

@Serializable(with = SafeableSerializer::class)
data class Safeable<T : Any>(val value: T?)

fun <T : Any> T?.asSafeable(): Safeable<T> = Safeable(this)

fun <T : Any> Collection<Safeable<T>>.extractSafeables(): Collection<T?> = map { it.value }
