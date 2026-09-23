package city.explorer.feature.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import city.explorer.domain.model.City
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun CityMapRoute(
    onBack: () -> Unit,
    onCitySelected: (City) -> Unit,
    viewModel: CityMapViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
    CityMapScreen(
        state = state,
        onBack = onBack,
        onSearchArea = viewModel::loadArea,
        onSelect = viewModel::select,
        onOpenDetails = onCitySelected,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityMapScreen(
    state: CityMapState,
    onBack: () -> Unit,
    onSearchArea: (Double, Double, Double) -> Unit,
    onSelect: (City?) -> Unit,
    onOpenDetails: (City) -> Unit,
    onRetry: () -> Unit,
) {
    var centerLat by remember { mutableDoubleStateOf(state.lastArea.centerLatitude) }
    var centerLng by remember { mutableDoubleStateOf(state.lastArea.centerLongitude) }
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.List, contentDescription = "Список")
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Map, contentDescription = "Карта", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
    ) { insets ->
        Box(Modifier.fillMaxSize().padding(insets)) {
            OpenStreetMap(
                cities = state.cities,
                onCenterChanged = { lat, lng ->
                    centerLat = lat
                    centerLng = lng
                },
                onMarkerSelected = onSelect,
            )
            Button(
                onClick = { onSearchArea(centerLat, centerLng, state.lastArea.radiusMeters) },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
                shape = RoundedCornerShape(18.dp),
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Искать здесь")
            }
            if (state.loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            state.error?.let {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRetry) { Text("Повторить") }
                }
            }
        }
    }

    state.selectedCity?.let { city ->
        ModalBottomSheet(onDismissRequest = { onSelect(null) }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(city.name, style = MaterialTheme.typography.headlineSmall)
                Text("${city.country} · Население ${city.population}")
                Button(
                    onClick = { onOpenDetails(city) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Подробнее")
                }
            }
        }
    }
}

@Composable
private fun OpenStreetMap(
    cities: List<City>,
    onCenterChanged: (Double, Double) -> Unit,
    onMarkerSelected: (City) -> Unit,
) {
    val context = LocalContext.current
    val map = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(5.0)
            controller.setCenter(GeoPoint(55.75204, 37.61781))
        }
    }
    DisposableEffect(map) {
        map.onResume()
        onDispose {
            map.onPause()
            map.onDetach()
        }
    }
    AndroidView(
        factory = { map },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            val previousCenter = view.mapCenter
            view.overlays.removeAll { it is Marker }
            cities.forEach { city ->
                Marker(view).apply {
                    position = GeoPoint(city.latitude, city.longitude)
                    title = city.name
                    snippet = city.country
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onMarkerSelected(city)
                        true
                    }
                    view.overlays += this
                }
            }
            view.controller.setCenter(previousCenter)
            view.setMapListener(
                object : org.osmdroid.events.MapListener {
                    override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                        val center = view.mapCenter
                        onCenterChanged(center.latitude, center.longitude)
                        return false
                    }

                    override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean = false
                },
            )
            view.invalidate()
        },
    )
}
