package theoneclick.client.shared.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent
import theoneclick.client.shared.navigation.models.routes.base.Route

interface NavigationController {

    val navigationEvents: SharedFlow<NavigationEvent>

    suspend fun sendNavigationEvent(navigationEvent: NavigationEvent)

    sealed interface NavigationEvent {
        data class Navigate(
            val destinationRoute: Route,
            val launchSingleTop: Boolean = false,
            val restoreState: Boolean = false,
            val popUpTo: PopUpTo? = null,
        ) : NavigationEvent {
            data class PopUpTo(
                val startRoute: Route,
                val isInclusive: Boolean,
                val saveState: Boolean,
            )
        }

        data object PopBackStack : NavigationEvent
    }
}

class DefaultNavigationController() : NavigationController {

    private val mutableNavigationEvents = MutableSharedFlow<NavigationEvent>()
    override val navigationEvents: SharedFlow<NavigationEvent> = mutableNavigationEvents

    override suspend fun sendNavigationEvent(navigationEvent: NavigationEvent) {
        mutableNavigationEvents.emit(navigationEvent)
    }
}
