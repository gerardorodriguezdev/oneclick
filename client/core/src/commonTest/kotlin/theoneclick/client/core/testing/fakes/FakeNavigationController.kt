package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.NavigationController.NavigationEvent

class FakeNavigationController(
    var emitNavigationEvents: Boolean = false,
) : NavigationController {

    val events = mutableListOf<NavigationEvent>()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    override val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents

    override suspend fun sendNavigationEvent(navigationEvent: NavigationEvent) {
        events.add(navigationEvent)
        if (emitNavigationEvents) {
            _navigationEvents.emit(navigationEvent)
        }
    }
}
