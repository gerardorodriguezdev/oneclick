@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewCompactScreen
import theoneclick.client.core.ui.previews.annotations.PreviewLargeScreen
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.HomeScreenScaffoldPreviewModels
import theoneclick.client.core.ui.screens.homeScreen.HomeScreenScaffoldPreview
import theoneclick.shared.core.models.routes.HomeRoute

@PreviewCompactScreen
@Composable
private fun HomeScreenScaffoldCompactScreenPreviews(
    @PreviewParameter(HomeScreenScaffoldCompactScreenPreviewProvider::class) previewModel: PreviewModel<HomeRoute>,
) {
    HomeScreenScaffoldPreview(previewModel)
}

private class HomeScreenScaffoldCompactScreenPreviewProvider : PreviewParameterProvider<PreviewModel<HomeRoute>> {
    override val values: Sequence<PreviewModel<HomeRoute>> =
        HomeScreenScaffoldPreviewModels().values.filter { previewModel -> previewModel.isCompact }
}

@PreviewLargeScreen
@Composable
private fun HomeScreenScaffoldLargeScreenPreviews(
    @PreviewParameter(HomeScreenScaffoldLargeScreenPreviewProvider::class) previewModel: PreviewModel<HomeRoute>,
) {
    HomeScreenScaffoldPreview(previewModel)
}

private class HomeScreenScaffoldLargeScreenPreviewProvider : PreviewParameterProvider<PreviewModel<HomeRoute>> {
    override val values: Sequence<PreviewModel<HomeRoute>> =
        HomeScreenScaffoldPreviewModels().values.filter { previewModel -> !previewModel.isCompact }
}
