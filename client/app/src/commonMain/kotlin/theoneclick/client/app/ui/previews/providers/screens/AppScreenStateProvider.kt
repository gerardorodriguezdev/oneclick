package theoneclick.client.app.ui.previews.providers.screens

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.screens.AppScreenState
import theoneclick.client.app.ui.screens.AppScreenState.NavigationBar
import theoneclick.client.app.ui.screens.AppScreenState.SnackbarState
import theoneclick.client.shared.ui.previews.providers.base.*
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute

class AppScreenStateProvider : PreviewParameterProvider<PreviewModel<AppScreenState>> {

    override val values: Sequence<PreviewModel<AppScreenState>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            lightThemeCompactPreviewModel(description = "BottomNavigationBar", model = withBottomNavigationBar),
            lightThemeCompactPreviewModel(description = "SuccessSnackbar", model = withSuccessSnackbar),
            lightThemeCompactPreviewModel(description = "ErrorSnackbar", model = withErrorSnackbar),
            lightThemeLargePreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            lightThemeLargePreviewModel(description = "StartNavigationBar", model = withStartNavigationBar),
            lightThemeLargePreviewModel(description = "SuccessSnackbar", model = withSuccessSnackbar),
            lightThemeLargePreviewModel(description = "ErrorSnackbar", model = withErrorSnackbar),

            darkThemeCompactPreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            darkThemeCompactPreviewModel(description = "BottomNavigationBar", model = withBottomNavigationBar),
            darkThemeCompactPreviewModel(description = "SuccessSnackbar", model = withSuccessSnackbar),
            darkThemeCompactPreviewModel(description = "ErrorSnackbar", model = withErrorSnackbar),
            darkThemeLargePreviewModel(description = "NoNavigationBar", model = noNavigationBar),
            darkThemeLargePreviewModel(description = "StartNavigationBar", model = withStartNavigationBar),
            darkThemeLargePreviewModel(description = "SuccessSnackbar", model = withSuccessSnackbar),
            darkThemeLargePreviewModel(description = "ErrorSnackbar", model = withErrorSnackbar),
        )

    companion object Companion {
        const val SNACKBAR_TEXT = "SnackBarText"
        private val successSnackbar = SnackbarState(
            text = SNACKBAR_TEXT,
            isError = false,
        )
        private val errorSnackbar = SnackbarState(
            text = SNACKBAR_TEXT,
            isError = true,
        )

        private val bottomNavigationBar = NavigationBar.Bottom(
            selectedRoute = NavigationBarRoute.DevicesList,
        )

        private val startNavigationBar = NavigationBar.Start(
            selectedRoute = NavigationBarRoute.DevicesList,
        )

        val noNavigationBar = appState()
        val withBottomNavigationBar = appState(bottomNavigationBar)
        val withStartNavigationBar = appState(startNavigationBar)

        val withSuccessSnackbar = appState(
            snackbarState = successSnackbar,
        )
        val withErrorSnackbar = appState(
            snackbarState = errorSnackbar,
        )

        private fun appState(
            navigationBar: NavigationBar? = null,
            snackbarState: SnackbarState? = null,
        ): AppScreenState =
            AppScreenState(
                navigationBar = navigationBar,
                snackbarState = snackbarState,
            )
    }
}
