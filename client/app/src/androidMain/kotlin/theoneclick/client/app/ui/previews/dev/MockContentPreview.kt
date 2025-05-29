@file:Suppress("UnusedPrivateMember")

package theoneclick.client.app.ui.previews.dev

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun MockContentPreview() {
    MockContent(modifier = Modifier.size(100.dp))
}
