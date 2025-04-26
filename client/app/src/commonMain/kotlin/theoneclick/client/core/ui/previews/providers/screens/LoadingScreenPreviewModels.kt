package theoneclick.client.core.ui.previews.providers.screens

import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.core.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.base.lightThemeCompactPreviewModel

class LoadingScreenPreviewModels : PreviewModelProvider<Unit> {

    override val values: Sequence<PreviewModel<Unit>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = Unit),
            darkThemeCompactPreviewModel(description = "Default", model = Unit),
        )
}
