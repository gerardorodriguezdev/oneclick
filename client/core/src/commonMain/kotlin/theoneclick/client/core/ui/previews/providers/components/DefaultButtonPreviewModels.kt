package theoneclick.client.core.ui.previews.providers.components

import theoneclick.client.core.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.core.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.components.DefaultButtonPreviewModels.DefaultButtonModel

class DefaultButtonPreviewModels : PreviewModelProvider<DefaultButtonModel> {

    override val values = sequenceOf(
        lightThemeCompactPreviewModel(description = "Disabled", model = disabledButtonModel),
        lightThemeCompactPreviewModel(description = "Enabled", model = enabledButtonModel),
        lightThemeCompactPreviewModel(description = "Loading", model = loadingButtonModel),

        darkThemeCompactPreviewModel(description = "Disabled", model = disabledButtonModel),
        darkThemeCompactPreviewModel(description = "Enabled", model = enabledButtonModel),
        darkThemeCompactPreviewModel(description = "Loading", model = loadingButtonModel),
    )

    companion object {
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
