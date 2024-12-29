package theoneclick.client.core.ui.previews.providers.components

import theoneclick.client.core.ui.components.SnackbarState
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.core.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.base.lightThemeLargePreviewModel

class DefaultScaffoldPreviewModels : PreviewModelProvider<SnackbarState> {
    override val values: Sequence<PreviewModel<SnackbarState>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = mockSnackbarState),
            lightThemeLargePreviewModel(description = "Default", model = mockSnackbarState),
        )

    companion object {
        val mockSnackbarState = SnackbarState(
            text = "Snackbar text",
            isErrorType = false,
            showSnackbar = true,
        )
    }
}
