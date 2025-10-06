package oneclick.client.shared.ui.previews.providers.shared

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import oneclick.client.shared.ui.previews.providers.base.PreviewModel
import oneclick.client.shared.ui.previews.providers.base.darkThemeCompactPreviewModel
import oneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel

class UnitProvider : PreviewParameterProvider<PreviewModel<Unit>> {

    override val values: Sequence<PreviewModel<Unit>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = Unit),
            darkThemeCompactPreviewModel(description = "Default", model = Unit),
        )
}
