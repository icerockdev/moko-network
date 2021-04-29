/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.safeable.Safeable
import dev.icerock.moko.network.safeable.SafeableSerializer
import dev.icerock.moko.network.safeable.asSafeable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class SafeableTest {

    @BeforeTest
    fun setup() {
        SafeableSerializer.deserializeExceptionHandler = null
    }

    @Test
    fun `safeable with default encode`() {
        val json = Json.Default
        val data = TestData()

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = "{}", actual = result)
    }

    @Test
    fun `safeable with null encode`() {
        val json = Json.Default
        val data = TestData(data = Safeable(value = null))

        assertFailsWith(SerializationException::class) {
            json.encodeToString(TestData.serializer(), data)
        }
    }

    @Test
    fun `safeable with value encode`() {
        val json = Json.Default
        val data = TestData(data = Safeable(value = TestData.TestEnum.ITEM2))

        val result = json.encodeToString(TestData.serializer(), data)
        assertEquals(expected = """{"data":"item2"}""", actual = result)
    }

    @Test
    fun `safeable with default decode`() {
        val json = Json.Default
        val input = "{}"

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(expected = TestData(), actual = result)
    }

    @Test
    fun `safeable with null decode`() {
        val json = Json.Default
        val input = """{"data":null}"""

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(
            expected = TestData(data = null),
            actual = result
        )
    }

    @Test
    fun `safeable with value decode`() {
        val json = Json.Default
        val input = """{"data":"item2"}"""

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(
            expected = TestData(
                data = Safeable(value = TestData.TestEnum.ITEM2)
            ),
            actual = result
        )
    }

    @Test
    fun `safeable with incorrect value decode`() {
        val json = Json.Default
        val input = """{"data":"item3"}"""

        val result = json.decodeFromString(TestData.serializer(), input)
        assertEquals(
            expected = TestData(
                data = Safeable(value = null)
            ),
            actual = result
        )
    }

    @Test
    fun `safeable deserialize handler test`() {
        val json = Json.Default
        val input = """{"data":"item3"}"""

        var serializationException: SerializationException? = null
        SafeableSerializer.deserializeExceptionHandler = {
            serializationException = it
            true
        }

        json.decodeFromString(TestData.serializer(), input)

        assertNotNull(serializationException)
    }

    @Test
    fun `safeable deserialize handler throws test`() {
        val json = Json.Default
        val input = """{"data":"item3"}"""

        SafeableSerializer.deserializeExceptionHandler = {
            false
        }

        assertFailsWith(SerializationException::class) {
            json.decodeFromString(TestData.serializer(), input)
        }
    }

    @Serializable
    data class TestData(
        val data: Safeable<TestEnum>? = TestEnum.ITEM1.asSafeable()
    ) {
        @Serializable
        enum class TestEnum {
            @SerialName("item1")
            ITEM1,

            @SerialName("item2")
            ITEM2
        }
    }
}
