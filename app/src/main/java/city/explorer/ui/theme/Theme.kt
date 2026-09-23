package city.explorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BrandPurple = Color(0xFF7C4DFF)
val Ink = Color(0xFF1F1D2B)
val Muted = Color(0xFF8F8A9A)
val SurfaceSoft = Color(0xFFF6F5F8)

private val LightColors = lightColorScheme(
    primary = BrandPurple,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = Muted,
)

private val DarkColors = darkColorScheme(primary = Color(0xFFB69CFF))

@Composable
fun CityExplorerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
