package theoneclick.client.core.models.results

sealed interface LogoutResult {
    data object Success : LogoutResult
    data object Failure : LogoutResult
}
