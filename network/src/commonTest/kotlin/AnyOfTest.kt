/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import dev.icerock.moko.network.exceptions.DataNotFitAnyOfSchema
import dev.icerock.moko.network.schemas.ComposedSchemaSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AnyOfTest {
    private val json = Json.Default

    @Test
    fun `anyOf decode PetByAge success`() {
        val input = """{"age":1}"""

        val output = json.decodeFromString(AnyOfPetByAgePetByType.serializer(), input)

        assertEquals(
            expected = AnyOfPetByAgePetByType(
                petByAge = PetByAge(age = 1),
                petByType = null
            ),
            actual = output
        )
    }

    @Test
    fun `anyOf decode PetByType success`() {
        val input = """{"pet_type":"Cat","hunts":true}"""

        val output = json.decodeFromString(AnyOfPetByAgePetByType.serializer(), input)

        assertEquals(
            expected = AnyOfPetByAgePetByType(
                petByAge = null,
                petByType = PetByType(petType = "Cat", hunts = true)
            ),
            actual = output
        )
    }

    @Test
    fun `anyOf decode PetByType and PetByAge success`() {
        val input = """{"pet_type":"Cat","hunts":true,"age":4}"""

        val output = json.decodeFromString(AnyOfPetByAgePetByType.serializer(), input)

        assertEquals(
            expected = AnyOfPetByAgePetByType(
                petByAge = PetByAge(age = 4),
                petByType = PetByType(petType = "Cat", hunts = true)
            ),
            actual = output
        )
    }

    @Test
    fun `anyOf decode PetByType and PetByAge failed`() {
        val input = """{"nickname": "Mr. Paws","hunts": false}"""

        assertFailsWith(DataNotFitAnyOfSchema::class) {
            json.decodeFromString(AnyOfPetByAgePetByType.serializer(), input)
        }
    }

    @Test
    fun `anyOf encode PetByAge`() {
        val input = AnyOfPetByAgePetByType(
            petByAge = PetByAge(age = 1),
            petByType = null
        )

        val output = json.encodeToString(AnyOfPetByAgePetByType.serializer(), input)

        assertEquals(
            expected = """{"age":1}""",
            actual = output
        )
    }

    @Test
    fun `anyOf encode PetByType`() {
        val input = AnyOfPetByAgePetByType(
            petByAge = null,
            petByType = PetByType(petType = "Cat", hunts = true)
        )

        val output = json.encodeToString(AnyOfPetByAgePetByType.serializer(), input)

        assertEquals(
            expected = """{"pet_type":"Cat","hunts":true}""",
            actual = output
        )
    }

    @Test
    fun `anyOf encode PetByType and PetByAge`() {
        val input = AnyOfPetByAgePetByType(
            petByAge = PetByAge(age = 4),
            petByType = PetByType(petType = "Cat", hunts = true)
        )

        val output = json.encodeToString(AnyOfPetByAgePetByType.serializer(), input)

        assertEquals(
            expected = """{"pet_type":"Cat","hunts":true,"age":4}""",
            actual = output
        )
    }
}


@Serializable
private data class PetByAge(
    val age: Int,
    val nickname: String? = null
)

@Serializable
private data class PetByType(
    @SerialName("pet_type")
    val petType: String,
    val hunts: Boolean? = null
)

@Serializable(with = AnyOfPetByAgePetByTypeSerializer::class)
private data class AnyOfPetByAgePetByType(
    val petByAge: PetByAge?,
    val petByType: PetByType?
)

private object AnyOfPetByAgePetByTypeSerializer : ComposedSchemaSerializer<AnyOfPetByAgePetByType>(
    serialName = "AnyOfPetByAgePetByTypeSerializer"
) {

    override fun decodeJson(json: Json, element: JsonElement): AnyOfPetByAgePetByType {
        val petByType = runCatching { json.decodeFromJsonElement(PetByType.serializer(), element) }
        val petByAge = runCatching { json.decodeFromJsonElement(PetByAge.serializer(), element) }

        ensureAnyItemIsSuccess(element, listOf(petByType, petByAge))

        return AnyOfPetByAgePetByType(
            petByType = petByType.getOrNull(),
            petByAge = petByAge.getOrNull()
        )
    }

    override fun encodeJson(json: Json, value: AnyOfPetByAgePetByType): List<JsonObject> {
        val petByType = value.petByType?.let {
            json.encodeToJsonElement(PetByType.serializer(), it)
        }
        val petByAge = value.petByAge?.let {
            json.encodeToJsonElement(PetByAge.serializer(), it)
        }

        return listOfNotNull(petByType, petByAge).map { it.jsonObject }
    }
}
