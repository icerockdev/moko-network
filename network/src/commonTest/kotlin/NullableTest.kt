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
    fun `nullable with default encode`() {
        val json = Json.Default
        val data = TestData()

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = "{}", actual = result)
    }

    @Test
    fun `nullable with null encode`() {
        val json = Json.Default
        val data = TestData(data = Nullable(value = null))

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = """{"data":null}""", actual = result)
    }

    @Test
    fun `nullable with value encode`() {
        val json = Json.Default
        val data = TestData(data = Nullable(value = "test"))

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = """{"data":"test"}""", actual = result)
    }


    @Test
    fun `nullable with default decode`() {
        val json = Json.Default
        val input = "{}"

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(expected = TestData(), actual = result)
    }

    @Test
    fun `nullable with null decode`() {
        val json = Json.Default
        val input = """{"data":null}"""

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(
            expected = TestData(data = null),
            actual = result
        ) // for now we cant check key exists
    }

    @Test
    fun `nullable with value decode`() {
        val json = Json.Default
        val input = """{"data":"test"}"""

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(expected = TestData(data = Nullable(value = "test")), actual = result)
    }

    @Serializable
    data class TestData(
        val data: Nullable<String>? = null
    )
}
