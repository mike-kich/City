package city.explorer.data.remote.dto

import city.explorer.domain.model.City
import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: Int,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val pop: Int,
) {
    fun toDomain() = City(
        id = id,
        name = name,
        country = country,
        latitude = lat,
        longitude = lon,
        population = pop,
    )
}

@Serializable
data class CitiesPageDto(
    val items: List<CityDto>,
    val limit: Int,
    val page: Int,
    val total: Int,
)

@Serializable
data class MapCitiesDto(
    val count: Int,
    val items: List<CityDto>,
)

@Serializable
data class ApiErrorDto(val error: String)
