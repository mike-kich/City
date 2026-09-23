package city.explorer.domain.model

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val population: Int,
)

data class CityPage(
    val items: List<City>,
    val page: Int,
    val limit: Int,
    val total: Int,
) {
    val hasNext: Boolean get() = page * limit < total
}

data class MapArea(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Double,
) {
    init {
        require(centerLatitude in -90.0..90.0)
        require(centerLongitude in -180.0..180.0)
        require(radiusMeters > 0.0)
    }
}
