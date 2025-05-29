package theoneclick.client.app.ui.previews.providers.screens

import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.app.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.app.ui.previews.providers.base.lightThemeCompactPreviewModel

class LoadingScreenPreviewModels : PreviewModelProvider<Unit> {

    override val values: Sequence<PreviewModel<Unit>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = Unit),
            darkThemeCompactPreviewModel(description = "Default", model = Unit),
        )
}
