package theoneclick.shared.core.dataSources.models.results

sealed interface UserLoggedResult {
    data object Logged : UserLoggedResult
    data object NotLogged : UserLoggedResult
    data object UnknownError : UserLoggedResult
}
