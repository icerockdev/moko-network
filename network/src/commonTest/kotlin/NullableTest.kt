/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.nullable.Nullable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class NullableTest {
    @Test
    fun `nullable with default`() {
        val json = Json.Default
        val data = TestData()

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = "{}", actual = result)
    }

    @Test
    fun `nullable with null`() {
        val json = Json.Default
        val data = TestData(data = Nullable(value = null))

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = """{"data":null}""", actual = result)
    }

    @Test
    fun `nullable with value`() {
        val json = Json.Default
        val data = TestData(data = Nullable(value = "test"))

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = """{"data":"test"}""", actual = result)
    }

    @Serializable
    data class TestData(
        val data: Nullable<String>? = null
    )
}
