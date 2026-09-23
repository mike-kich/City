package city.explorer.feature.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CityDetailsRoute(
    cityId: Int,
    onBack: () -> Unit,
) {
    val viewModel: CityDetailsViewModel = koinViewModel { parametersOf(cityId) }
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is CityDetailsSideEffect.OpenBrowser ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(effect.url)))
        }
    }
    CityDetailsScreen(state, onBack, viewModel::searchInBrowser)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityDetailsScreen(
    state: CityDetailsState,
    onBack: () -> Unit,
    onSearch: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Информация о городе") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
    ) { insets ->
        val city = state.city
        if (city == null) {
            Column(Modifier.fillMaxSize().padding(insets).padding(24.dp)) {
                Text("Информация о городе недоступна")
                Spacer(Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Вернуться") }
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(insets).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(city.name, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text(city.country, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow("Население", NumberFormat.getIntegerInstance().format(city.population))
                    DetailRow("Широта", city.latitude.toString())
                    DetailRow("Долгота", city.longitude.toString())
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Outlined.OpenInBrowser, contentDescription = null)
                Text("Поиск информации о городе", modifier = Modifier.padding(start = 10.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
