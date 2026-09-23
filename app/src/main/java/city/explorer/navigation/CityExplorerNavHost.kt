package city.explorer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import city.explorer.feature.details.CityDetailsRoute
import city.explorer.feature.map.CityMapRoute
import city.explorer.feature.search.CitySearchRoute

private const val SearchRoute = "cities"
private const val DetailsRoute = "city/{cityId}"
private const val MapRoute = "map"

@Composable
fun CityExplorerNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SearchRoute) {
        composable(SearchRoute) {
            CitySearchRoute(
                onCitySelected = { navController.navigate("city/${it.id}") },
                onOpenMap = { navController.navigate(MapRoute) },
            )
        }
        composable(
            route = DetailsRoute,
            arguments = listOf(navArgument("cityId") { type = NavType.IntType }),
        ) { entry ->
            CityDetailsRoute(
                cityId = entry.arguments?.getInt("cityId") ?: return@composable,
                onBack = navController::navigateUp,
            )
        }
        composable(MapRoute) {
            CityMapRoute(
                onBack = navController::navigateUp,
                onCitySelected = { navController.navigate("city/${it.id}") },
            )
        }
    }
}
