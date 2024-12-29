package theoneclick.client.core.ui.previews.providers.base

interface PreviewModelProvider<T> {
    val values: Sequence<PreviewModel<T>>
}
