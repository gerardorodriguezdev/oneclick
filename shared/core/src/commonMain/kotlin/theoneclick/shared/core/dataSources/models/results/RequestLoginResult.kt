package theoneclick.shared.core.dataSources.models.results

import theoneclick.shared.core.routes.AppRoute

sealed interface RequestLoginResult {

    sealed interface ValidLogin : RequestLoginResult {
        data class LocalRedirect(val appRoute: AppRoute) :
            ValidLogin
        data class ExternalRedirect(val urlString: String) :
            ValidLogin
    }

    data object UnknownError : RequestLoginResult
}
