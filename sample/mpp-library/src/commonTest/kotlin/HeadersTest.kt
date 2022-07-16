/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respondOk
import kotlin.test.Test
import kotlin.test.assertTrue

class HeadersTest {
    private lateinit var httpClient: HttpClient

    @Test
    fun `auth test`() {
        httpClient = createMockClient { request ->
            assertTrue { request.headers.contains("Authorization") }
            respondOk()
        }
    }
}
