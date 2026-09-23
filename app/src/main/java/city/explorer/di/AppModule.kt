package city.explorer.di

import city.explorer.BuildConfig
import city.explorer.data.remote.CityApi
import city.explorer.data.remote.KtorCityApi
import city.explorer.data.remote.createHttpClient
import city.explorer.data.repository.DefaultCityRepository
import city.explorer.domain.repository.CityRepository
import city.explorer.feature.details.CityDetailsViewModel
import city.explorer.feature.map.CityMapViewModel
import city.explorer.feature.search.CitySearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient(BuildConfig.DEBUG) }
    single<CityApi> { KtorCityApi(get(), BuildConfig.API_BASE_URL) }
    single<CityRepository> { DefaultCityRepository(get()) }
    viewModel { CitySearchViewModel(get()) }
    viewModel { parameters -> CityDetailsViewModel(parameters.get(), get()) }
    viewModel { CityMapViewModel(get()) }
}
