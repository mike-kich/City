package city.explorer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import city.explorer.domain.model.City
import city.explorer.domain.repository.CityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class CitySearchState(
    val query: String = "",
    val cities: List<City> = emptyList(),
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    val page: Int = 0,
    val canLoadMore: Boolean = true,
    val error: String? = null,
)

sealed interface CitySearchSideEffect {
    data class OpenCity(val city: City) : CitySearchSideEffect
}

class CitySearchViewModel(
    private val repository: CityRepository,
) : ViewModel(), ContainerHost<CitySearchState, CitySearchSideEffect> {
    override val container = container<CitySearchState, CitySearchSideEffect>(CitySearchState()) {
        loadFirst("")
    }

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) = intent {
        reduce { state.copy(query = query, error = null) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            search(query)
        }
    }

    fun retry() = intent { loadFirst(state.query) }

    private fun search(query: String) = intent { loadFirst(query) }

    fun loadMore() = intent {
        if (state.loading || state.loadingMore || !state.canLoadMore) return@intent
        val requestQuery = state.query
        val nextPage = state.page + 1
        reduce { state.copy(loadingMore = true, error = null) }
        runCatching { repository.search(requestQuery, nextPage, PAGE_SIZE) }
            .onSuccess { page ->
                if (state.query == requestQuery) {
                    reduce {
                        state.copy(
                            cities = (state.cities + page.items).distinctBy(City::id),
                            loadingMore = false,
                            page = page.page,
                            canLoadMore = page.hasNext,
                        )
                    }
                }
            }
            .onFailure {
                reduce { state.copy(loadingMore = false, error = it.userMessage()) }
            }
    }

    fun openCity(city: City) = intent {
        repository.remember(city)
        postSideEffect(CitySearchSideEffect.OpenCity(city))
    }

    private suspend fun org.orbitmvi.orbit.syntax.Syntax<CitySearchState, CitySearchSideEffect>.loadFirst(
        query: String,
    ) {
        val normalized = query.trim()
        reduce {
            state.copy(
                loading = true,
                loadingMore = false,
                cities = emptyList(),
                page = 0,
                canLoadMore = true,
                error = null,
            )
        }
        runCatching { repository.search(normalized, 1, PAGE_SIZE) }
            .onSuccess { page ->
                if (state.query.trim() == normalized) {
                    reduce {
                        state.copy(
                            cities = page.items,
                            loading = false,
                            page = page.page,
                            canLoadMore = page.hasNext,
                        )
                    }
                }
            }
            .onFailure {
                if (state.query.trim() == normalized) {
                    reduce { state.copy(loading = false, error = it.userMessage()) }
                }
            }
    }

    private fun Throwable.userMessage(): String =
        "Не удалось загрузить города. Проверьте подключение и попробуйте снова."

    private companion object {
        const val PAGE_SIZE = 20
    }
}
