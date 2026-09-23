package city.explorer.domain.repository

import city.explorer.domain.model.City
import city.explorer.domain.model.CityPage
import city.explorer.domain.model.MapArea

interface CityRepository {
    suspend fun search(query: String, page: Int, limit: Int): CityPage
    suspend fun around(area: MapArea): List<City>
    fun remember(city: City)
    fun city(id: Int): City?
}
