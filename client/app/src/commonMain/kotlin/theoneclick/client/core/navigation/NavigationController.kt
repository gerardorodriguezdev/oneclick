package theoneclick.client.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import theoneclick.client.core.navigation.NavigationController.NavigationEvent
import theoneclick.shared.core.models.routes.base.Route
import theoneclick.shared.core.platform.AppLogger

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

class DefaultNavigationController(
    private val appLogger: AppLogger,
) : NavigationController {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    override val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents

    override suspend fun sendNavigationEvent(navigationEvent: NavigationEvent) {
        appLogger.i("Emitting navigation event '$navigationEvent'")
        _navigationEvents.emit(navigationEvent)
    }
}
