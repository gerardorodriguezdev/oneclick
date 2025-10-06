package oneclick.client.shared.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import oneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.shared.ui.previews.providers.base.PreviewModel

@Composable
fun Header(
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = textAlign,
        modifier = modifier.semantics {
            heading()
        },
    )
}

@Composable
fun Title(
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
fun Body(
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
fun Label(
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
internal fun HeaderPreview(previewModel: PreviewModel<Unit>) {
    ScreenPreviewComposable(previewModel) {
        Header(text = "Sample text")
    }
}

@Composable
internal fun TitlePreview(previewModel: PreviewModel<Unit>) {
    ScreenPreviewComposable(previewModel) {
        Title(text = "Sample text")
    }
}

@Composable
internal fun BodyPreview(previewModel: PreviewModel<Unit>) {
    ScreenPreviewComposable(previewModel) {
        Body(text = "Sample text")
    }
}

@Composable
internal fun LabelPreview(previewModel: PreviewModel<Unit>) {
    ScreenPreviewComposable(previewModel) {
        Label(text = "Sample text")
    }
}
