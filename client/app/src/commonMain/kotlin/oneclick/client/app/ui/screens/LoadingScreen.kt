package oneclick.client.app.ui.screens

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import oneclick.client.app.ui.screens.LoadingScreenTestTags.PROGRESS_INDICATOR_TEST_TAG
import oneclick.client.shared.ui.components.ScreenBox
import oneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.shared.ui.previews.providers.base.PreviewModel

@Composable
fun LoadingScreen() {
    ScreenBox {
        CircularProgressIndicator(
            modifier = Modifier.testTag(PROGRESS_INDICATOR_TEST_TAG)
        )
    }
}

object LoadingScreenTestTags {
    const val PROGRESS_INDICATOR_TEST_TAG = "LoadingScreen.ProgressIndicator"
}

@Composable
fun LoadingScreenPreview(previewModel: PreviewModel<Unit>) {
    ScreenPreviewComposable(previewModel) {
        LoadingScreen()
    }
}
