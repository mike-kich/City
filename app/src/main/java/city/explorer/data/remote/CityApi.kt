package city.explorer.data.remote

import city.explorer.data.remote.dto.CitiesPageDto
import city.explorer.data.remote.dto.MapCitiesDto

interface CityApi {
    suspend fun cities(query: String, page: Int, limit: Int): CitiesPageDto
    suspend fun map(centerLat: Double, centerLng: Double, radiusMeters: Double): MapCitiesDto
}
