package city.explorer.data.repository

import city.explorer.data.remote.CityApi
import city.explorer.data.remote.dto.CitiesPageDto
import city.explorer.data.remote.dto.CityDto
import city.explorer.data.remote.dto.MapCitiesDto
import city.explorer.domain.model.MapArea
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Test

class DefaultCityRepositoryTest {
    private val city = CityDto(
        id = 1,
        name = "Moscow",
        country = "RU",
        lat = 55.75204,
        lon = 37.61781,
        pop = 12_655_050,
    )
    private val api = FakeCityApi(city)
    private val repository = DefaultCityRepository(api)

    @Test
    fun `search maps page and caches city`() = runTest {
        val result = repository.search("  mos ", page = 1, limit = 20)

        assertEquals("mos", api.lastQuery)
        assertEquals("Moscow", result.items.single().name)
        assertFalse(result.hasNext)
        assertSame(result.items.single(), repository.city(1))
    }

    @Test
    fun `map passes center and radius`() = runTest {
        val area = MapArea(55.75, 37.61, 10_000.0)

        val result = repository.around(area)

        assertEquals(1, result.size)
        assertEquals(area, api.lastArea)
    }

    private class FakeCityApi(private val city: CityDto) : CityApi {
        var lastQuery: String? = null
        var lastArea: MapArea? = null

        override suspend fun cities(query: String, page: Int, limit: Int): CitiesPageDto {
            lastQuery = query
            return CitiesPageDto(listOf(city), limit, page, total = 1)
        }

        override suspend fun map(
            centerLat: Double,
            centerLng: Double,
            radiusMeters: Double,
        ): MapCitiesDto {
            lastArea = MapArea(centerLat, centerLng, radiusMeters)
            return MapCitiesDto(1, listOf(city))
        }
    }
}
