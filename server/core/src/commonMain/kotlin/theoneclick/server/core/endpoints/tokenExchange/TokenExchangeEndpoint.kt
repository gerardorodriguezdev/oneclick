package theoneclick.server.core.endpoints.tokenExchange

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.data.models.UserData
import theoneclick.server.core.data.validators.ParamsValidator
import theoneclick.server.core.data.validators.ParamsValidator.TokenExchangeValidationResult
import theoneclick.server.core.data.validators.ParamsValidator.TokenExchangeValidationResult.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.*
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.data.models.endpoints.ServerEndpoints

fun Routing.tokenExchangeEndpoint() {
    val userDataSource: UserDataSource by inject()
    val securityUtils: SecurityUtils by inject()
    val paramsValidator: ParamsValidator by inject()

    post(
        endpoint = ServerEndpoints.TOKEN_EXCHANGE,
        paramsParsing = { call.receiveParameters().tokenExchangeParams() },
        paramsValidation = paramsValidator::isTokenExchangeParamsValid,
    ) { tokenExchangeValidationResult: TokenExchangeValidationResult ->
        when (tokenExchangeValidationResult) {
            is ValidAuthorizationCodeType -> {
                handleAuthorizationCode(
                    userData = tokenExchangeValidationResult.userData,
                    userDataSource = userDataSource,
                    securityUtils = securityUtils,
                )
            }

            is ValidRefreshTokenType -> {
                handleRefreshToken(
                    userData = tokenExchangeValidationResult.userData,
                    userDataSource = userDataSource,
                    securityUtils = securityUtils,
                )
            }

            is InvalidTokenExchangeParams -> call.respond(HttpStatusCode.BadRequest)
        }
    }
}

private suspend fun RoutingContext.handleAuthorizationCode(
    userData: UserData,
    userDataSource: UserDataSource,
    securityUtils: SecurityUtils,
) {
    val accessToken = securityUtils.encryptedToken()
    val refreshToken = securityUtils.encryptedToken()

    userDataSource.saveUserData(
        userData.copy(
            authorizationCode = null,
            state = null,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    )

    call.respond(
        TokenExchangeResponse(
            accessToken = accessToken.value,
            refreshToken = refreshToken.value,
        )
    )
}

private suspend fun RoutingContext.handleRefreshToken(
    userData: UserData,
    userDataSource: UserDataSource,
    securityUtils: SecurityUtils,
) {
    val accessToken = securityUtils.encryptedToken()

    userDataSource.saveUserData(
        userData.copy(
            authorizationCode = null,
            state = null,
            accessToken = accessToken,
        )
    )

    call.respond(
        TokenExchangeResponse(
            accessToken = accessToken.value,
            refreshToken = null,
        )
    )
}

private fun Parameters.tokenExchangeParams(): TokenExchangeParams =
    TokenExchangeParams(
        clientId = clientId,
        clientSecret = clientSecret,
        grantType = grantType,
        authorizationCode = code,
        refreshToken = refreshToken,
        redirectUri = redirectUri,
    )
