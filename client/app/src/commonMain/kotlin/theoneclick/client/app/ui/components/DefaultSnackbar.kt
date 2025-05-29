package theoneclick.client.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import theoneclick.client.app.ui.components.DefaultSnackbarTestTags.SNACKBAR_TEST_TAG
import theoneclick.client.app.ui.previews.dev.ComponentPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.components.DefaultSnackbarPreviewModels.DefaultSnackbarModel

@Composable
fun DefaultSnackbar(snackbarData: SnackbarData, isError: Boolean = false) {
    Snackbar(
        containerColor = containerColor(isError),
        snackbarData = snackbarData,
        modifier = Modifier.testTag(SNACKBAR_TEST_TAG)
    )
}

@Composable
private fun containerColor(isError: Boolean) =
    if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        SnackbarDefaults.color
    }

object DefaultSnackbarTestTags {
    const val SNACKBAR_TEST_TAG = "DefaultSnackbar.Container"
}

@Composable
fun DefaultSnackbarPreview(previewModel: PreviewModel<DefaultSnackbarModel>) {
    ComponentPreviewComposable(previewModel) {
        DefaultSnackbar(
            snackbarData = previewModel.model.snackbarData,
            isError = previewModel.model.isError,
        )
    }
}
