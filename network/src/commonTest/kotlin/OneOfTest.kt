/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.exceptions.DataNotFitOneOfSchema
import dev.icerock.moko.network.schemas.ComposedSchemaSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// for now oneOf implementation is replace to JsonElement - we can't correctly support oneOf by
// specification examples
// https://swagger.io/docs/specification/data-models/oneof-anyof-allof-not/#oneof
@Ignore
class OneOfTest {
    private val json = Json.Default

    @Test
    fun `oneOf decode Doge success`() {
        val input = """{"bark":true,"breed":"Dingo"}"""

        val output = json.decodeFromString(OneOfDogCat.serializer(), input)

        assertEquals(
            expected = OneOfDogCat(
                dog = Doge(bark = true, breed = "Dingo"),
                cat = null
            ),
            actual = output
        )
    }

    @Test
    fun `oneOf decode Cat success`() {
        val input = """{"hunts":true}"""

        val output = json.decodeFromString(OneOfDogCat.serializer(), input)

        assertEquals(
            expected = OneOfDogCat(
                dog = null,
                cat = Cat(hunts = true)
            ),
            actual = output
        )
    }

    @Test
    fun `oneOf decode Doge & Cat fails`() {
        val input = """{"bark":true,"hunts":true,"breed":"Husky","age":3}"""

        assertFailsWith(DataNotFitOneOfSchema::class) {
            json.decodeFromString(OneOfDogCat.serializer(), input)
        }
    }

    @Test
    fun `oneOf decode invalid data fails`() {
        val input = """{"bark":true,"hunts":true}"""

        assertFailsWith(DataNotFitOneOfSchema::class) {
            json.decodeFromString(OneOfDogCat.serializer(), input)
        }
    }

    @Test
    fun `oneOf encode Doge`() {
        val input = OneOfDogCat(
            dog = Doge(bark = true, breed = "Dingo"),
            cat = null
        )

        val output = json.encodeToString(OneOfDogCat.serializer(), input)

        assertEquals(
            expected = """{"bark":true,"breed":"Dingo"}""",
            actual = output
        )
    }

    @Test
    fun `oneOf encode Cat`() {
        val input = OneOfDogCat(
            dog = null,
            cat = Cat(hunts = true)
        )

        val output = json.encodeToString(OneOfDogCat.serializer(), input)

        assertEquals(
            expected = """{"hunts":true}""",
            actual = output
        )
    }
}


@Serializable
private data class Doge(
    val bark: Boolean? = null,
    val breed: String? = null
)

@Serializable
private data class Cat(
    val hunts: Boolean? = null,
    val age: Int? = null
)

@Serializable(with = OneOfDogCatSerializer::class)
private data class OneOfDogCat(
    val dog: Doge?,
    val cat: Cat?
)

private object OneOfDogCatSerializer : ComposedSchemaSerializer<OneOfDogCat>(
    serialName = "OneOfDogCatSerializer"
) {

    override fun decodeJson(json: Json, element: JsonElement): OneOfDogCat {
        val doge = runCatching { json.decodeFromJsonElement(Doge.serializer(), element) }
        val cat = runCatching { json.decodeFromJsonElement(Cat.serializer(), element) }

        ensureOnlyOneItemIsSuccess(element, listOf(doge, cat))

        return OneOfDogCat(
            dog = doge.getOrNull(),
            cat = cat.getOrNull()
        )
    }

    override fun encodeJson(json: Json, value: OneOfDogCat): List<JsonObject> {
        val dog = value.dog?.let {
            json.encodeToJsonElement(Doge.serializer(), it)
        }
        val cat = value.cat?.let {
            json.encodeToJsonElement(Cat.serializer(), it)
        }

        return listOfNotNull(dog, cat).map { it.jsonObject }
    }
}
