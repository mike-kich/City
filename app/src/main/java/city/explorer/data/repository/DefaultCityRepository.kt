package city.explorer.data.repository

import city.explorer.data.remote.CityApi
import city.explorer.domain.model.City
import city.explorer.domain.model.CityPage
import city.explorer.domain.model.MapArea
import city.explorer.domain.repository.CityRepository
import java.util.concurrent.ConcurrentHashMap

class DefaultCityRepository(
    private val api: CityApi,
) : CityRepository {
    private val cache = ConcurrentHashMap<Int, City>()

    override suspend fun search(query: String, page: Int, limit: Int): CityPage {
        val response = api.cities(query.trim(), page, limit)
        val cities = response.items.map { it.toDomain() }
        cities.forEach(::remember)
        return CityPage(cities, response.page, response.limit, response.total)
    }

    override suspend fun around(area: MapArea): List<City> {
        val cities = api.map(
            centerLat = area.centerLatitude,
            centerLng = area.centerLongitude,
            radiusMeters = area.radiusMeters,
        ).items.map { it.toDomain() }
        cities.forEach(::remember)
        return cities
    }

    override fun remember(city: City) {
        cache[city.id] = city
    }

    override fun city(id: Int): City? = cache[id]
}
