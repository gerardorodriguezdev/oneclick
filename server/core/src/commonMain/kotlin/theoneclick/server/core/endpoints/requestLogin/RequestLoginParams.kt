package theoneclick.server.core.endpoints.requestLogin

import theoneclick.server.core.endpoints.authorize.AuthorizeParams

data class RequestLoginParams(
    val username: String,
    val password: String,
    val authorizeParams: AuthorizeParams?
)
