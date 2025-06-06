package theoneclick.client.shared.ui.previews.providers.shared

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel

class UnitProvider : PreviewParameterProvider<PreviewModel<Unit>> {

    override val values: Sequence<PreviewModel<Unit>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = Unit),
            darkThemeCompactPreviewModel(description = "Default", model = Unit),
        )
}