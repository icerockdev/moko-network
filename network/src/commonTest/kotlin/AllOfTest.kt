/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class AllOfTest {
    private val json = Json {
        ignoreUnknownKeys = true
    }

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

    @Serializable
    data class Pet(
        @SerialName("pet_type")
        val petType: String
    )

    // allOf
    @Serializable(with = DogSerializer::class)
    data class Dog(
        val pet: Pet,
        val inlineDog: InlineDog
    )

    // allOf
    @Serializable
    data class Cat(
        val pet: Pet,
        val inlineCat: InlineCat
    )

    @Serializable
    data class InlineDog(
        val bark: Boolean? = null,
        val breed: String? = null
    )

    @Serializable
    data class InlineCat(
        val hunts: Boolean? = null,
        val age: Int? = null
    )

    // oneOf / anyOf
    @Serializable
    data class CatOrDog(
        val cat: Cat? = null,
        val dog: Dog? = null
    )
}

object DogJsonSerializer : JsonTransformingSerializer<AllOfTest.Dog>(AllOfTest.Dog.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return buildJsonObject {
            put("pet", buildJsonObject {
                element.jsonObject["pet_type"]?.let { put("pet_type", it) }
            })
            put("inlineDog", buildJsonObject {
                element.jsonObject["bark"]?.let { put("bark", it) }
                element.jsonObject["breed"]?.let { put("breed", it) }
            })
        }
    }
}

object DogSerializer : KSerializer<AllOfTest.Dog> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DogSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AllOfTest.Dog {
        decoder as JsonDecoder
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val pet = decoder.json.decodeFromJsonElement(AllOfTest.Pet.serializer(), jsonElement)
        val inlineDog =
            decoder.json.decodeFromJsonElement(AllOfTest.InlineDog.serializer(), jsonElement)

        return AllOfTest.Dog(
            pet = pet,
            inlineDog = inlineDog
        )
    }

    override fun serialize(encoder: Encoder, value: AllOfTest.Dog) {
        encoder as JsonEncoder
        val petJson = encoder.json.encodeToJsonElement(AllOfTest.Pet.serializer(), value.pet)
        val inlineDogJson =
            encoder.json.encodeToJsonElement(AllOfTest.InlineDog.serializer(), value.inlineDog)

        val outputObject = buildJsonObject {
            petJson.jsonObject.entries.forEach {
                put(it.key, it.value)
            }
            inlineDogJson.jsonObject.entries.forEach {
                put(it.key, it.value)
            }
        }

        encoder.encodeJsonElement(outputObject)
    }
}
