package theoneclick.client.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import theoneclick.client.core.navigation.NavigationController.NavigationEvent
import theoneclick.client.core.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.core.navigation.NavigationController.NavigationEvent.PopBackStack

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

class RealNavigationControllerObserver(
    override val navigationController: NavigationController,
    override val navHostController: NavHostController,
) : NavigationControllerObserver {

    override fun onNavigationEvent(navigationEvent: NavigationEvent) {
        when (navigationEvent) {
            is Navigate -> {
                navHostController.navigate(route = navigationEvent.destinationRoute) {
                    navigationEvent.popUpTo?.let { popUpTo ->
                        popUpTo(popUpTo.startRoute) {
                            inclusive = popUpTo.isInclusive
                            saveState = popUpTo.saveState
                        }
                    }
                }
            }

            is PopBackStack -> navHostController.popBackStack()
        }
    }
}

@Composable
fun rememberNavigationObserver(
    navigationController: NavigationController,
    navHostController: NavHostController,
): NavigationControllerObserver =
    remember {
        RealNavigationControllerObserver(navigationController, navHostController)
    }
