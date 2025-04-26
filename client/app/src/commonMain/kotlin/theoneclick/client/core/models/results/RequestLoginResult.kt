package theoneclick.client.core.models.results

sealed interface RequestLoginResult {
    data object ValidLogin : RequestLoginResult
    data object Failure : RequestLoginResult
}
