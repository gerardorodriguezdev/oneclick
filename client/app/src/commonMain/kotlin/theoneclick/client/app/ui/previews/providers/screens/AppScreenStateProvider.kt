package theoneclick.client.app.ui.previews.providers.screens

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.providers.base.*
import theoneclick.client.app.ui.screens.AppScreenState
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute

class AppScreenStateProvider : PreviewParameterProvider<PreviewModel<AppScreenState>> {

    override val values: Sequence<PreviewModel<AppScreenState>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            lightThemeCompactPreviewModel(description = "NoNavigationBar", model = withBottomNavigationBar),
            lightThemeLargePreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            lightThemeLargePreviewModel(description = "NoNavigationBar", model = withStartNavigationBar),

            darkThemeCompactPreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            darkThemeCompactPreviewModel(description = "NoNavigationBar", model = withBottomNavigationBar),
            darkThemeLargePreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            darkThemeLargePreviewModel(description = "NoNavigationBar", model = withStartNavigationBar),
        )

    companion object Companion {
        private val bottomNavigationBar = AppScreenState.NavigationBar.Bottom(
            selectedRoute = NavigationBarRoute.DevicesList,
        )

        private val startNavigationBar = AppScreenState.NavigationBar.Start(
            selectedRoute = NavigationBarRoute.DevicesList,
        )

        val noNavigationBar = appState()
        val withBottomNavigationBar = appState(bottomNavigationBar)
        val withStartNavigationBar = appState(startNavigationBar)

        private fun appState(navigationBar: AppScreenState.NavigationBar? = null): AppScreenState =
            AppScreenState(
                navigationBar = navigationBar,
            )
    }
}
