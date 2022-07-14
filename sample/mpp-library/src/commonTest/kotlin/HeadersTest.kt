import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import openapi.requestHeader.apis.AuthApi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class HeadersTest {
    private lateinit var httpClient: HttpClient
    private lateinit var json: Json
    private lateinit var authApi: AuthApi

    @BeforeTest
    fun setup() {
        json = Json.Default

        httpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respondOk(content = petstoreSearchResponse)
                }
            }
        }

        authApi = AuthApi(
            httpClient = httpClient,
            json = json,
            basePath = "https://api.github.com/"
        )
    }

    @Test
    fun `auth test`() {
        val result = runBlocking {
            authApi.auth("token ghp_sdDSFGdfsdewrcfgtium56sgs")
        }
    }
}
