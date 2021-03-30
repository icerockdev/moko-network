/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.schemas.AllOfSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class AllOfTest {
    private val json = Json.Default

    @Test
    fun `allOf decode`() {
        val input = """{"pet_type":"Dog","bark":false,"breed":"Dingo"}"""

        val output = json.decodeFromString(Dog.serializer(), input)

        assertEquals(
            expected = Dog(
                pet = Pet(petType = "Dog"),
                inlineDog = InlineDog(bark = false, breed = "Dingo")
            ),
            actual = output
        )
    }

    @Test
    fun `allOf encode`() {
        val input = Dog(
            pet = Pet(petType = "Dog"),
            inlineDog = InlineDog(bark = false, breed = "Dingo")
        )

        val output = json.encodeToString(Dog.serializer(), input)

        assertEquals(
            expected = """{"pet_type":"Dog","bark":false,"breed":"Dingo"}""",
            actual = output
        )
    }
}


@Serializable
private data class Pet(
    @SerialName("pet_type")
    val petType: String
)

// allOf
@Serializable(with = DogSerializer::class)
private data class Dog(
    val pet: Pet,
    val inlineDog: InlineDog
)

// allOf
@Serializable
private data class Cat(
    val pet: Pet,
    val inlineCat: InlineCat
)

@Serializable
private data class InlineDog(
    val bark: Boolean? = null,
    val breed: String? = null
)

@Serializable
private data class InlineCat(
    val hunts: Boolean? = null,
    val age: Int? = null
)

private object DogSerializer : AllOfSerializer<Dog>("DogSerializer") {

    override fun decodeJson(json: Json, element: JsonElement): Dog {
        val pet = json.decodeFromJsonElement(Pet.serializer(), element)
        val inlineDog = json.decodeFromJsonElement(InlineDog.serializer(), element)

        return Dog(
            pet = pet,
            inlineDog = inlineDog
        )
    }

    override fun encodeJson(json: Json, value: Dog): List<JsonObject> {
        val pet = json.encodeToJsonElement(Pet.serializer(), value.pet).jsonObject
        val inlineDog = json.encodeToJsonElement(InlineDog.serializer(), value.inlineDog).jsonObject

        return listOf(pet, inlineDog)
    }
}
