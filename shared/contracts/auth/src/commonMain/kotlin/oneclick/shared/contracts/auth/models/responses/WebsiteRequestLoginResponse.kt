package oneclick.shared.contracts.auth.models.responses

import kotlinx.serialization.Serializable

sealed interface WebsiteRequestLoginResponse {
    @Serializable
    data object ValidLogin : WebsiteRequestLoginResponse

    @Serializable
    data object WaitForApproval : WebsiteRequestLoginResponse
}