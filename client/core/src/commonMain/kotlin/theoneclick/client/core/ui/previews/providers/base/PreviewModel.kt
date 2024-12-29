package theoneclick.client.core.ui.previews.providers.base

data class PreviewModel<T>(
    val description: String,
    val isDarkTheme: Boolean,
    val isCompact: Boolean,
    val model: T,
)

fun <T> lightThemeCompactPreviewModel(description: String, model: T): PreviewModel<T> =
    PreviewModel(
        description = "LightThemeCompactPreview$description",
        isDarkTheme = false,
        isCompact = true,
        model = model
    )

fun <T> lightThemeLargePreviewModel(description: String, model: T): PreviewModel<T> =
    PreviewModel(
        description = "LightThemeLargePreview$description",
        isDarkTheme = false,
        isCompact = false,
        model = model
    )

fun <T> darkThemeCompactPreviewModel(description: String, model: T): PreviewModel<T> =
    PreviewModel(
        description = "DarkThemeCompactPreview$description",
        isDarkTheme = true,
        isCompact = true,
        model = model
    )

fun <T> darkThemeLargePreviewModel(description: String, model: T): PreviewModel<T> =
    PreviewModel(
        description = "DarkThemeLargePreview$description",
        isDarkTheme = true,
        isCompact = false,
        model = model
    )
