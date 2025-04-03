package theoneclick.client.core.ui.previews.providers.screens.homeScreen

import theoneclick.client.core.ui.previews.providers.base.*
import theoneclick.shared.core.models.routes.HomeRoute

class HomeScreenScaffoldPreviewModels : PreviewModelProvider<HomeRoute> {

    override val values: Sequence<PreviewModel<HomeRoute>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "AddDeviceSelected", model = HomeRoute.AddDevice),
            lightThemeCompactPreviewModel(description = "DevicesListSelected", model = HomeRoute.DevicesList),
            lightThemeCompactPreviewModel(description = "UserSettingsSelected", model = HomeRoute.UserSettings),

            lightThemeLargePreviewModel(description = "AddDeviceSelected", model = HomeRoute.AddDevice),
            lightThemeLargePreviewModel(description = "DevicesListSelected", model = HomeRoute.DevicesList),
            lightThemeLargePreviewModel(description = "UserSettingsSelected", model = HomeRoute.UserSettings),

            darkThemeCompactPreviewModel(description = "AddDeviceSelected", model = HomeRoute.AddDevice),
            darkThemeCompactPreviewModel(description = "DevicesListSelected", model = HomeRoute.DevicesList),
            darkThemeCompactPreviewModel(description = "UserSettingsSelected", model = HomeRoute.UserSettings),

            darkThemeLargePreviewModel(description = "AddDeviceSelected", model = HomeRoute.AddDevice),
            darkThemeLargePreviewModel(description = "DevicesListSelected", model = HomeRoute.DevicesList),
            darkThemeLargePreviewModel(description = "UserSettingsSelected", model = HomeRoute.UserSettings),
        )
}
