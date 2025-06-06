package theoneclick.client.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.screens.LoadingScreenTestTags.PROGRESS_INDICATOR_TEST_TAG

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .testTag(PROGRESS_INDICATOR_TEST_TAG)
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