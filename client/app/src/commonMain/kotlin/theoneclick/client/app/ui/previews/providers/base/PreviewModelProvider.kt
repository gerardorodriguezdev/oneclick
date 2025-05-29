package theoneclick.client.app.ui.previews.providers.base

interface PreviewModelProvider<T> {
    val values: Sequence<PreviewModel<T>>
}
