package theoneclick.client.features.home.ui.events

internal sealed interface HomesListEvent {
    data object Refresh : HomesListEvent
}
