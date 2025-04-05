package theoneclick.client.core.ui.previews.annotations

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    locale = "en",
    fontScale = 2f,
    widthDp = 360,
    heightDp = 640,
)
annotation class PreviewCompactScreen

@Preview(
    locale = "es",
    fontScale = 1f,
    widthDp = 1_280,
    heightDp = 800,
)
annotation class PreviewLargeScreen

@PreviewCompactScreen
@PreviewLargeScreen
annotation class PreviewScreens
