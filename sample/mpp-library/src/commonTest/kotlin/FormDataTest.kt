/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import cases.formData.apis.AuthApi
import cases.formData.models.Response
import cases.formData.models.SignupRequest
import dev.icerock.moko.network.multipart.MultiPartContent
import io.ktor.client.engine.mock.*
import io.ktor.client.request.forms.*
import io.ktor.http.ContentType
import io.ktor.http.content.MultiPartData
import io.ktor.util.AttributeKey
import io.ktor.util.toCharArray
import io.ktor.utils.io.bits.Memory
import io.ktor.utils.io.bits.storeByteArray
import io.ktor.utils.io.core.AbstractInput
import io.ktor.utils.io.core.ExperimentalIoApi
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.internal.ChunkBuffer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okio.Buffer
import okio.Source
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormDataTest {
    @Test
    fun `formData body`() {
        val data: ByteArray = mockData()
        val source: Source = data.toSource()
        val input: Input = source.asInput()

        val api = createApi { request ->
            val body = request.body
            assertTrue(body is MultiPartFormDataContent)
            // TODO #122 fix test and logic of generated formdata support
            assertContains(body.toByteArray().decodeToString(), "SignupRequest(firstName=first, lastName=last, phone=+799, email=a@b, password=111, passwordRepeat=111, countryId=1, cityId=2, company=test, middleName=null, post=null, interests=null)")
            assertContains(body.toByteArray().decodeToString(), data.decodeToString())

            respondOk(
                """
{
    "status": 200,
    "message": "ok",
    "timestamp": 1001.0,
    "success": true
}
            """.trimIndent()
            )
        }

        val result = runBlocking {
            api.signup(
                signup = SignupRequest(
                    firstName = "first",
                    lastName = "last",
                    phone = "+799",
                    email = "a@b",
                    password = "111",
                    passwordRepeat = "111",
                    countryId = 1,
                    cityId = 2,
                    company = "test"
                ),
                avatar = input
            )
        }

        assertEquals(
            expected = Response(
                status = 200,
                message = "ok",
                timestamp = 1001.0,
                success = true
            ),
            actual = result
        )
    }

    private fun mockData(): ByteArray {
        val random: Random = Random.Default
        return ByteArray(8) { random.nextBytes(1)[0] }
    }

    private fun ByteArray.toSource(): Source = Buffer().also { it.write(this) }

    private fun createApi(mock: MockRequestHandler): AuthApi {
        val json = Json.Default
        val httpClient = createMockClient(json, mock)
        return AuthApi(
            basePath = "https://localhost",
            httpClient = httpClient,
            json = json
        )
    }
}

internal class SourceAsInput(
    private val source: Source
) : @Suppress("DEPRECATION") AbstractInput(pool = ChunkBuffer.Pool) {

    @OptIn(ExperimentalIoApi::class)
    override fun fill(destination: Memory, offset: Int, length: Int): Int {
        println("fill $offset $length")
        val buffer = Buffer()
        source.read(buffer, length.toLong())

        val chunkByteArray: ByteArray = buffer.readByteArray()
        destination.storeByteArray(offset, chunkByteArray)

        return chunkByteArray.size
    }

    override fun closeSource() {
        source.close()
    }
}

fun Source.asInput(): Input = SourceAsInput(this)
