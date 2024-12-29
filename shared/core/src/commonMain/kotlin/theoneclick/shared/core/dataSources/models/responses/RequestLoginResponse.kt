package theoneclick.shared.core.dataSources.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.core.routes.AppRoute

@Serializable
sealed interface RequestLoginResponse {

    @Serializable
    data class LocalRedirect(val appRoute: AppRoute) :
        RequestLoginResponse

    @Serializable
    data class ExternalRedirect(val urlString: String) :
        RequestLoginResponse
}
