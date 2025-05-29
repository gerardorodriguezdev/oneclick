package theoneclick.client.app.ui.previews.providers.components

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.app.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.app.ui.previews.providers.components.DefaultButtonModelProvider.DefaultButtonModel

class DefaultButtonModelProvider : PreviewParameterProvider<PreviewModel<DefaultButtonModel>> {

    override val values = sequenceOf(
        lightThemeCompactPreviewModel(description = "Disabled", model = disabledButtonModel),
        lightThemeCompactPreviewModel(description = "Enabled", model = enabledButtonModel),
        lightThemeCompactPreviewModel(description = "Loading", model = loadingButtonModel),

        darkThemeCompactPreviewModel(description = "Disabled", model = disabledButtonModel),
        darkThemeCompactPreviewModel(description = "Enabled", model = enabledButtonModel),
        darkThemeCompactPreviewModel(description = "Loading", model = loadingButtonModel),
    )

    companion object Companion {
        const val BUTTON_TEXT = "ButtonText"

        val disabledButtonModel = DefaultButtonModel(
            isEnabled = false,
        )

        val enabledButtonModel = DefaultButtonModel(
            isEnabled = true,
        )

        val loadingButtonModel = DefaultButtonModel(
            isLoading = true,
        )
    }

    data class DefaultButtonModel(
        val text: String = BUTTON_TEXT,
        val isEnabled: Boolean = false,
        val isLoading: Boolean = false,
    )
}
