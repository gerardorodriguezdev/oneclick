package oneclick.client.apps.user.core.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.apps.user.navigation.NavigationController.NavigationEvent.Navigate
import oneclick.client.apps.user.navigation.models.routes.AppRoute
import oneclick.client.apps.user.navigation.popUpToInclusive
import oneclick.client.shared.network.models.UserLoggedResult
import oneclick.client.shared.network.platform.AuthenticationDataSource

@Inject
class InitViewModel(
    private val navigationController: NavigationController,
    private val authenticationDataSource: AuthenticationDataSource,
) : ViewModel() {
    private var isSignedRequestJob: Job? = null

    init {
        handleNavigation()
    }

    private fun handleNavigation() {
        isSignedRequestJob?.cancel()

        isSignedRequestJob =
            viewModelScope.launch {
                val userLoggedResult = authenticationDataSource.isUserLogged()
                when (userLoggedResult) {
                    is UserLoggedResult.Logged -> handleUserLogged()
                    is UserLoggedResult.NotLogged -> handleUserNotLogged()
                    is UserLoggedResult.UnknownError -> handleUserNotLogged()
                }
            }
    }

    private suspend fun handleUserLogged() {
        navigationController.sendNavigationEvent(
            Navigate(
                destinationRoute = AppRoute.Home,
                launchSingleTop = true,
                popUpTo = popUpToInclusive(startRoute = AppRoute.Init)
            )
        )
    }

    private suspend fun handleUserNotLogged() {
        navigationController.sendNavigationEvent(
            Navigate(
                destinationRoute = AppRoute.Login,
                launchSingleTop = true,
                popUpTo = popUpToInclusive(startRoute = AppRoute.Init)
            )
        )
    }

    override fun onCleared() {
        super.onCleared()

        isSignedRequestJob?.cancel()
    }
}
