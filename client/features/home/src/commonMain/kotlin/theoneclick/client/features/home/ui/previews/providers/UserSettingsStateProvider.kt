package theoneclick.client.features.home.ui.previews.providers

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.features.home.states.UserSettingsState
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel

internal class UserSettingsStateProvider : PreviewParameterProvider<PreviewModel<UserSettingsState>> {
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

    companion object Companion {
        val initState = UserSettingsState()
        val disabledState = UserSettingsState(isButtonEnabled = false)
        val loadingState = UserSettingsState(isLoading = true)
        val errorState = UserSettingsState(showError = true)
        val successState = UserSettingsState(showSuccess = true)
    }
}
