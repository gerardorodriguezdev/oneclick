package theoneclick.client.shared.ui.previews.providers.components

import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.shared.ui.components.DefaultSnackbarState
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel

class DefaultSnackbarModelProvider : PreviewParameterProvider<PreviewModel<DefaultSnackbarState>> {

    override val values: Sequence<PreviewModel<DefaultSnackbarState>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Error", model = errorDefaultSnackbarState),
            lightThemeCompactPreviewModel(description = "Success", model = successDefaultSnackbarState),

            darkThemeCompactPreviewModel(description = "Error", model = errorDefaultSnackbarState),
            darkThemeCompactPreviewModel(description = "Success", model = successDefaultSnackbarState),
        )

    companion object {
        val mockSnackbarData = object : SnackbarData {
            override val visuals: SnackbarVisuals = object : SnackbarVisuals {
                override val actionLabel: String? = null
                override val duration: SnackbarDuration = SnackbarDuration.Short
                override val message: String = "Message"
                override val withDismissAction: Boolean = false
            }

            override fun dismiss() {}

            override fun performAction() {}
        }

        val errorDefaultSnackbarState = DefaultSnackbarState(
            snackbarData = mockSnackbarData,
            isError = true,
        )

        val successDefaultSnackbarState = DefaultSnackbarState(
            snackbarData = mockSnackbarData,
            isError = false,
        )
    }
}
