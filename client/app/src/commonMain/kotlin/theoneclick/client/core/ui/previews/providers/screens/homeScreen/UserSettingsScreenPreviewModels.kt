package theoneclick.client.core.ui.previews.providers.screens.homeScreen

import theoneclick.client.core.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.core.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.core.ui.states.homeScreen.UserSettingsState

class UserSettingsScreenPreviewModels : PreviewModelProvider<UserSettingsState> {
    override val values = sequenceOf(
        lightThemeCompactPreviewModel(description = "Init", model = initState),
        lightThemeCompactPreviewModel(description = "DisabledState", model = disabledState),
        lightThemeCompactPreviewModel(description = "LoadingState", model = loadingState),
        lightThemeCompactPreviewModel(description = "ErrorState", model = errorState),
        lightThemeCompactPreviewModel(description = "SuccessState", model = successState),

        darkThemeCompactPreviewModel(description = "Init", model = initState),
        darkThemeCompactPreviewModel(description = "DisabledState", model = disabledState),
        darkThemeCompactPreviewModel(description = "LoadingState", model = loadingState),
        darkThemeCompactPreviewModel(description = "ErrorState", model = errorState),
        darkThemeCompactPreviewModel(description = "SuccessState", model = successState),
    )

    companion object {
        val initState = UserSettingsState()
        val disabledState = UserSettingsState(isButtonEnabled = false)
        val loadingState = UserSettingsState(isLoading = true)
        val errorState = UserSettingsState(showError = true)
        val successState = UserSettingsState(showSuccess = true)
    }
}
