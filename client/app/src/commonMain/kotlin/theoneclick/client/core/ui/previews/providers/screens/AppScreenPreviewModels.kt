package theoneclick.client.core.ui.previews.providers.screens

import theoneclick.client.core.ui.previews.providers.base.*
import theoneclick.client.core.ui.screens.AppScreenState
import theoneclick.shared.core.models.routes.HomeRoute

class AppScreenPreviewModels : PreviewModelProvider<AppScreenState> {

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

    companion object {
        private val bottomNavigationBar = AppScreenState.NavigationBar.Bottom(
            selectedRoute = HomeRoute.DevicesList,
        )

        private val startNavigationBar = AppScreenState.NavigationBar.Start(
            selectedRoute = HomeRoute.DevicesList,
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