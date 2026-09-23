package city.explorer.feature.map

import androidx.lifecycle.ViewModel
import city.explorer.domain.model.City
import city.explorer.domain.model.MapArea
import city.explorer.domain.repository.CityRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class CityMapState(
    val cities: List<City> = emptyList(),
    val selectedCity: City? = null,
    val loading: Boolean = true,
    val error: String? = null,
    val lastArea: MapArea = MapArea(55.75204, 37.61781, 50_000.0),
)

class CityMapViewModel(
    private val repository: CityRepository,
) : ViewModel(), ContainerHost<CityMapState, Nothing> {
    override val container = container<CityMapState, Nothing>(CityMapState()) {
        load(state.lastArea)
    }

    fun loadArea(latitude: Double, longitude: Double, radiusMeters: Double) = intent {
        load(
            MapArea(
                centerLatitude = latitude.coerceIn(-90.0, 90.0),
                centerLongitude = longitude.coerceIn(-180.0, 180.0),
                radiusMeters = radiusMeters.coerceIn(1_000.0, 500_000.0),
            ),
        )
    }

    fun select(city: City?) = intent {
        city?.let(repository::remember)
        reduce { state.copy(selectedCity = city) }
    }

    fun retry() = intent { load(state.lastArea) }

    private suspend fun org.orbitmvi.orbit.syntax.Syntax<CityMapState, Nothing>.load(area: MapArea) {
        reduce { state.copy(loading = true, error = null, lastArea = area) }
        runCatching { repository.around(area) }
            .onSuccess { cities ->
                reduce { state.copy(cities = cities, loading = false) }
            }
            .onFailure {
                reduce {
                    state.copy(
                        loading = false,
                        error = "Не удалось загрузить города на карте",
                    )
                }
            }
    }
}
