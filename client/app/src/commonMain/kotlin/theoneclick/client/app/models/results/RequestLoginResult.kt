package theoneclick.client.app.models.results

sealed interface RequestLoginResult {
    data object ValidLogin : RequestLoginResult
    data object Failure : RequestLoginResult
}
