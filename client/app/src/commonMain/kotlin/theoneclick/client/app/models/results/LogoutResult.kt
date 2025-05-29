package theoneclick.client.app.models.results

sealed interface LogoutResult {
    data object Success : LogoutResult
    data object Failure : LogoutResult
}
