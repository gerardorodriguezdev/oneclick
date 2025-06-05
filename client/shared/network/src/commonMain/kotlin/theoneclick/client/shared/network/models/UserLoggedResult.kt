package theoneclick.client.shared.network.models

sealed interface UserLoggedResult {
    data object Logged : UserLoggedResult
    data object NotLogged : UserLoggedResult
    data object UnknownError : UserLoggedResult
}
