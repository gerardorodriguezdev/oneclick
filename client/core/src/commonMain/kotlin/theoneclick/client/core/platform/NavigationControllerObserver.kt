package theoneclick.client.core.platform

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.NavigationController.NavigationEvent

interface NavigationControllerObserver {
    val navigationController: NavigationController
    val navHostController: NavHostController

    suspend fun subscribe() {
        coroutineScope {
            launch {
                navigationController.navigationEvents.collect(::onNavigationEvent)
            }
        }
    }

    fun onNavigationEvent(navigationEvent: NavigationEvent)
}

@Composable
expect fun rememberNavigationObserver(
    navigationController: NavigationController,
    navHostController: NavHostController,
): NavigationControllerObserver
