/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package tests.utils

import java.io.InputStream
import kotlin.test.assertNotNull

actual fun Any.readResourceText(path: String): String {
    val classLoader: ClassLoader? = this.javaClass.classLoader
    assertNotNull(classLoader, "can't get classLoader of $this")
    val resource: InputStream? = classLoader.getResourceAsStream(path)
    assertNotNull(resource, "can't find resource with path [$path]")
    return resource
        .bufferedReader()
        .readText()
}
