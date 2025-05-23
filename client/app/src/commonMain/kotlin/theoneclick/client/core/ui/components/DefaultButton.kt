package theoneclick.client.core.ui.components

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
import theoneclick.client.core.ui.components.DefaultButtonTestTags.BUTTON_PROGRESS_INDICATOR_TEST_TAG
import theoneclick.client.core.ui.components.DefaultButtonTestTags.BUTTON_TEST_TAG
import theoneclick.client.core.ui.components.DefaultButtonTestTags.BUTTON_TEXT_TEST_TAG
import theoneclick.client.core.ui.previews.dev.ComponentPreviewComposable
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.components.DefaultButtonPreviewModels.DefaultButtonModel

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.testTag(BUTTON_TEST_TAG),
        enabled = isEnabled,
    ) {
        Box(modifier = Modifier.sizeIn(minHeight = 40.dp)) {
            AnimatedContent(
                targetState = isLoading,
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.Center),
            ) { targetState ->
                if (targetState) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag(BUTTON_PROGRESS_INDICATOR_TEST_TAG),
                    )
                } else {
                    Text(
                        text = text,
                        modifier = Modifier.testTag(BUTTON_TEXT_TEST_TAG)
                    )
                }
            }
        }
    }
}

object DefaultButtonTestTags {
    const val BUTTON_TEST_TAG = "DefaultButton.Container"
    const val BUTTON_TEXT_TEST_TAG = "DefaultButton.Text"
    const val BUTTON_PROGRESS_INDICATOR_TEST_TAG = "DefaultButton.ProgressIndicator"
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
