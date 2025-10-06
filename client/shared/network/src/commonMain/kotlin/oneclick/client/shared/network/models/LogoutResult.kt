package oneclick.client.shared.network.models

sealed interface LogoutResult {
    data object Success : LogoutResult
    data object Error : LogoutResult
}
