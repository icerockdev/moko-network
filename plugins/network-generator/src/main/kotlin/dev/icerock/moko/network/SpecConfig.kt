/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class SpecConfig @Inject constructor(objectFactory: ObjectFactory) {

    internal val specs: NamedDomainObjectContainer<SpecInfo> =
        objectFactory.domainObjectContainer(SpecInfo::class.java)

    fun spec(name: String, setup: SpecInfo.() -> Unit) {
        specs.create(name).setup()
    }
}
