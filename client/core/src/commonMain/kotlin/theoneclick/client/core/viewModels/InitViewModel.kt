package theoneclick.client.core.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.NavigationController.NavigationEvent.Navigate
import theoneclick.client.core.dataSources.AuthenticationDataSource
import theoneclick.shared.core.models.results.UserLoggedResult
import theoneclick.shared.core.models.routes.AppRoute

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
                authenticationDataSource
                    .isUserLogged()
                    .collect { userLoggedResult ->
                        when (userLoggedResult) {
                            is UserLoggedResult.Logged -> handleUserLogged()
                            is UserLoggedResult.NotLogged -> handleUserNotLogged()
                            is UserLoggedResult.UnknownError -> handleUserNotLogged()
                        }
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
