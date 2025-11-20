package oneclick.client.apps.user.ui.previews.providers.shared

import oneclick.client.apps.user.ui.previews.providers.base.PreviewModel
import oneclick.client.apps.user.ui.previews.providers.base.darkThemeCompactPreviewModel
import oneclick.client.apps.user.ui.previews.providers.base.lightThemeCompactPreviewModel
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class UnitProvider : PreviewParameterProvider<PreviewModel<Unit>> {

    override val values: Sequence<PreviewModel<Unit>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Default", model = Unit),
            darkThemeCompactPreviewModel(description = "Default", model = Unit),
        )
}
