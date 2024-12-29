package theoneclick.client.core.ui.screenProperties

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Suppress("CompositionLocalAllowlist")
val LocalScreenProperties = staticCompositionLocalOf { ScreenProperties(isCompact = true) }

@Immutable
class ScreenProperties(val isCompact: Boolean)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ScreenProperties(content: @Composable () -> Unit) {
    val windowSizeClass = calculateWindowSizeClass()
    val isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val screenProperties = ScreenProperties(isCompact = isCompactScreen)

    CompositionLocalProvider(LocalScreenProperties provides screenProperties) {
        content()
    }
}
