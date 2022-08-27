/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package tests.utils

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile
import kotlin.test.assertNotNull

actual fun Any.readResourceText(path: String): String {
    val pathWithoutExtension: String = path.substringBeforeLast(".")
    val extension = path.substringAfterLast(".")
    val filePath: String? = NSBundle.mainBundle
        .pathForResource("resources/$pathWithoutExtension", extension)
    assertNotNull(filePath, "can't find file on path [$filePath]")
    return NSString.stringWithContentsOfFile(filePath) as String
}
