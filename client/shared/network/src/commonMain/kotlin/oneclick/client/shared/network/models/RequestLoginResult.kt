package oneclick.client.shared.network.models

sealed interface RequestLoginResult {
    data object ValidLogin : RequestLoginResult
    data object Error : RequestLoginResult
}
