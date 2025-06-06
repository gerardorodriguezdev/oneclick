package theoneclick.client.shared.ui.previews.providers.components

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.shared.ui.components.SnackbarState
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeLargePreviewModel

class SnackbarStateProvider : PreviewParameterProvider<PreviewModel<SnackbarState>> {
    override val values: Sequence<PreviewModel<SnackbarState>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = mockSnackbarState),
            lightThemeLargePreviewModel(description = "Default", model = mockSnackbarState),
        )

    companion object Companion {
        val mockSnackbarState = SnackbarState(
            text = "Snackbar text",
            isErrorType = false,
            showSnackbar = true,
        )
    }
}
