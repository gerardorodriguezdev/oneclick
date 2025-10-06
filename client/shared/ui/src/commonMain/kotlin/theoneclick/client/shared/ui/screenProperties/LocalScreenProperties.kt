package oneclick.client.shared.ui.screenProperties

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalScreenProperties = staticCompositionLocalOf { ScreenProperties(isCompact = true) }

data class ScreenProperties(val isCompact: Boolean)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ScreenProperties(content: @Composable () -> Unit) {
    val windowSizeClass = calculateWindowSizeClass()
    val screenProperties = remember(windowSizeClass) {
        val isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
        ScreenProperties(isCompact = isCompactScreen)
    }

    CompositionLocalProvider(LocalScreenProperties provides screenProperties) {
        content()
    }
}
