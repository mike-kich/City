package city.explorer.feature.details

import androidx.lifecycle.ViewModel
import city.explorer.domain.model.City
import city.explorer.domain.repository.CityRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class CityDetailsState(val city: City?)

sealed interface CityDetailsSideEffect {
    data class OpenBrowser(val url: String) : CityDetailsSideEffect
}

class CityDetailsViewModel(
    cityId: Int,
    private val repository: CityRepository,
) : ViewModel(), ContainerHost<CityDetailsState, CityDetailsSideEffect> {
    override val container = container<CityDetailsState, CityDetailsSideEffect>(
        CityDetailsState(repository.city(cityId)),
    )

    fun searchInBrowser() = intent {
        val name = state.city?.name ?: return@intent
        val query = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
        postSideEffect(CityDetailsSideEffect.OpenBrowser("https://www.google.com/search?q=$query"))
    }
}
