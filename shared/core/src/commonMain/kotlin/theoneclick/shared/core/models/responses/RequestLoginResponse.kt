package theoneclick.shared.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.routes.AppRoute

@Serializable
sealed interface RequestLoginResponse {

    @Serializable
    data class LocalRedirect(val appRoute: AppRoute) :
        RequestLoginResponse

    @Serializable
    data class ExternalRedirect(val urlString: String) :
        RequestLoginResponse
}
