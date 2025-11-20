package oneclick.client.apps.user.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import oneclick.client.apps.user.ui.components.DialogBoxTestTags.HEADER
import oneclick.client.apps.user.ui.previews.dev.MockContent
import oneclick.client.apps.user.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.apps.user.ui.previews.providers.base.PreviewModel
import oneclick.client.apps.user.ui.theme.Tokens

@Composable
fun DialogBox(
    modifier: Modifier = Modifier,
    header: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(Tokens.containerPadding)
        ) {
            header?.let {
                Header(
                    text = header,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag(HEADER),
                )
            }

            content()
        }
    }
}

object DialogBoxTestTags {
    const val HEADER = "DialogBox.Header"
}

@Composable
internal fun DialogBoxPreview(previewModel: PreviewModel<Unit>) {
    ScreenPreviewComposable(previewModel) {
        DialogBox {
            MockContent(modifier = Modifier.size(100.dp))
        }
    }
}
