package city.explorer.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class KtorCityApiTest {
    @Test
    fun `cities sends documented query parameters and decodes response`() = runTest {
        var requestedUrl = ""
        val engine = MockEngine { request ->
            requestedUrl = request.url.toString()
            respond(
                content = """{"items":[{"id":1,"name":"Moscow","country":"RU","lat":55.75,"lon":37.61,"pop":12655050}],"limit":20,"page":1,"total":1}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val response = KtorCityApi(client, "http://example.test").cities("Moscow", 1, 20)

        assertEquals(1, response.items.size)
        assertEquals("Moscow", response.items.single().name)
        assertEquals(
            "http://example.test/api/cities?query=Moscow&page=1&limit=20",
            requestedUrl,
        )
    }
}
