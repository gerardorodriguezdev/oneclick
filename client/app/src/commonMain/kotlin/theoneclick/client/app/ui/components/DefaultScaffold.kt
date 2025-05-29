package theoneclick.client.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import theoneclick.client.app.ui.previews.dev.MockContent
import theoneclick.client.app.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel

@Composable
fun DefaultScaffold(
    snackbarState: SnackbarState,
    onSnackbarShow: () -> Unit,
    content: @Composable (paddingValue: PaddingValues) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    DefaultSnackbar(snackbarData = snackbarData, isError = snackbarState.isErrorType)
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        content(paddingValues)
    }

    val onSnackbarShown by rememberUpdatedState(onSnackbarShow)
    LaunchedEffect(snackbarState.showSnackbar) {
        if (snackbarState.showSnackbar) {
            snackbarHostState.showSnackbar(snackbarState.text)
            onSnackbarShown()
        }
    }
}

data class SnackbarState(
    val text: String,
    val isErrorType: Boolean = false,
    val showSnackbar: Boolean = false,
)

@Composable
fun DefaultScaffoldPreview(previewModel: PreviewModel<SnackbarState>) {
    ScreenPreviewComposable(previewModel) {
        DefaultScaffold(
            snackbarState = previewModel.model,
            onSnackbarShow = {},
            content = {
                MockContent(modifier = Modifier.fillMaxSize())
            }
        )
    }
}
