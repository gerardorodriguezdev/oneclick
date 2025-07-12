package theoneclick.client.shared.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import theoneclick.client.shared.ui.components.DefaultSnackbarTestTags.SNACKBAR
import theoneclick.client.shared.ui.previews.dev.ComponentPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel

@Composable
fun DefaultSnackbar(state: DefaultSnackbarState) {
    Snackbar(
        containerColor = containerColor(state.isError),
        snackbarData = state.snackbarData,
        modifier = Modifier.testTag(SNACKBAR)
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
    const val SNACKBAR = "DefaultSnackbar.Container"
}

data class DefaultSnackbarState(
    val snackbarData: SnackbarData,
    val isError: Boolean,
)

@Composable
fun DefaultSnackbarPreview(previewModel: PreviewModel<DefaultSnackbarState>) {
    ComponentPreviewComposable(previewModel) {
        DefaultSnackbar(
            state = previewModel.model,
        )
    }
}
