package theoneclick.client.shared.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import theoneclick.client.shared.ui.components.DefaultButtonTestTags.BUTTON_PROGRESS_INDICATOR
import theoneclick.client.shared.ui.components.DefaultButtonTestTags.BUTTON_TEXT
import theoneclick.client.shared.ui.previews.dev.ComponentPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.components.DefaultButtonModelProvider.DefaultButtonModel

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled,
    ) {
        Box(modifier = Modifier.sizeIn(minHeight = DefaultButtonConstants.minHeight)) {
            AnimatedContent(
                targetState = isLoading,
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.Center),
            ) { targetState ->
                if (targetState) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag(BUTTON_PROGRESS_INDICATOR),
                    )
                } else {
                    Text(
                        text = text,
                        modifier = Modifier.testTag(BUTTON_TEXT)
                    )
                }
            }
        }
    }
}

object DefaultButtonTestTags {
    const val BUTTON_TEXT = "DefaultButton.Text"
    const val BUTTON_PROGRESS_INDICATOR = "DefaultButton.ProgressIndicator"
}

object DefaultButtonConstants {
    val minHeight = 40.dp
}

@Composable
fun DefaultButtonPreview(previewModel: PreviewModel<DefaultButtonModel>) {
    ComponentPreviewComposable(previewModel) {
        DefaultButton(
            text = previewModel.model.text,
            onClick = {},
            isLoading = previewModel.model.isLoading,
            isEnabled = previewModel.model.isEnabled,
        )
    }
}