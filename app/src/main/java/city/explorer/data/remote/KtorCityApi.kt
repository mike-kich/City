package city.explorer.data.remote

import city.explorer.data.remote.dto.CitiesPageDto
import city.explorer.data.remote.dto.MapCitiesDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorCityApi(
    private val client: HttpClient,
    private val baseUrl: String,
) : CityApi {
    override suspend fun cities(query: String, page: Int, limit: Int): CitiesPageDto =
        client.get("$baseUrl/api/cities") {
            parameter("query", query.trim())
            parameter("page", page.coerceAtLeast(1))
            parameter("limit", limit.coerceIn(1, 100))
        }.body()

    override suspend fun map(
        centerLat: Double,
        centerLng: Double,
        radiusMeters: Double,
    ): MapCitiesDto = client.get("$baseUrl/api/cities/map") {
        parameter("centerLat", centerLat)
        parameter("centerLng", centerLng)
        parameter("radius", radiusMeters)
    }.body()
}
