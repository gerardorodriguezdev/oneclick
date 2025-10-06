package oneclick.client.shared.ui.previews.dev

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MockContent(modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = Color.Red) {}
}

@Preview
@Composable
private fun MockContentPreview() {
    MockContent(modifier = Modifier.size(100.dp))
}
