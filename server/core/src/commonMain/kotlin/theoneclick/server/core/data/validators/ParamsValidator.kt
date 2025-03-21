package theoneclick.server.core.data.validators

import theoneclick.server.core.data.models.GoogleHomeActionsRedirectUrl
import theoneclick.server.core.data.models.UserData
import theoneclick.server.core.data.models.UserSession
import theoneclick.server.core.data.validators.ParamsValidator.AccessTokenValidationResult.InvalidAccessToken
import theoneclick.server.core.data.validators.ParamsValidator.AccessTokenValidationResult.ValidAccessToken
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult.InvalidAuthorizeParams
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult.ValidAuthorizeParams
import theoneclick.server.core.data.validators.ParamsValidator.FulfillmentRequestValidationResult.InvalidFulfillmentRequest
import theoneclick.server.core.data.validators.ParamsValidator.FulfillmentRequestValidationResult.ValidFulfillmentRequest
import theoneclick.server.core.data.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.core.data.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.server.core.data.validators.ParamsValidator.TokenExchangeValidationResult.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.endpoints.authorize.AuthorizeParams
import theoneclick.server.core.endpoints.fulfillment.FulfillmentRequest
import theoneclick.server.core.endpoints.fulfillment.FulfillmentRequest.Input.Intent
import theoneclick.server.core.endpoints.requestLogin.RequestLoginParams
import theoneclick.server.core.endpoints.tokenExchange.TokenExchangeParams
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.shared.core.validators.deviceIdValidator
import theoneclick.shared.core.validators.deviceNameValidator
import theoneclick.shared.core.validators.encryptedTokenValidator
import theoneclick.shared.core.validators.passwordValidator
import theoneclick.shared.core.validators.roomNameValidator
import theoneclick.shared.core.validators.usernameValidator
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.timeProvider.TimeProvider

@Suppress("TooManyFunctions")
class ParamsValidator(
    private val environment: Environment,
    private val timeProvider: TimeProvider,
    private val securityUtils: SecurityUtils,
    private val userDataSource: UserDataSource,
) {

    fun isRequestLoginParamsValid(requestLoginParams: RequestLoginParams): RequestLoginValidationResult {
        val username = requestLoginParams.username
        val password = requestLoginParams.password

        return when {
            usernameValidator.isNotValid(username) -> InvalidRequestLoginParams
            passwordValidator.isNotValid(password) -> InvalidRequestLoginParams
            else -> isLoginValid(
                username = username,
                password = password,
                authorizeParams = requestLoginParams.authorizeParams,
            )
        }
    }

    private fun isLoginValid(
        username: String,
        password: String,
        authorizeParams: AuthorizeParams?,
    ): RequestLoginValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> ValidRequestLogin.RegistrableUser(
                username = username,
                password = password,
                authorizeValidationResult = isAuthorizeParamsValid(authorizeParams),
            )

            userData.username != username -> InvalidRequestLoginParams

            !securityUtils.verifyPassword(
                password = password,
                hashedPassword = userData.hashedPassword,
            ) -> InvalidRequestLoginParams

            else -> ValidRequestLogin.ValidUser(
                userData,
                isAuthorizeParamsValid(authorizeParams)
            )
        }
    }

    fun isAuthorizeParamsValid(authorizeParams: AuthorizeParams?): AuthorizeValidationResult {
        val redirectUri = GoogleHomeActionsRedirectUrl.create(environment.secretGoogleHomeActionsClientId).value

        return when {
            authorizeParams == null -> InvalidAuthorizeParams
            environment.secretGoogleHomeActionsClientId != authorizeParams.clientId -> InvalidAuthorizeParams
            redirectUri != authorizeParams.redirectUri -> InvalidAuthorizeParams
            AuthorizeParams.RESPONSE_TYPE_CODE != authorizeParams.responseType -> InvalidAuthorizeParams
            authorizeParams.state == null -> InvalidAuthorizeParams
            else -> handleUserDataValidationForAuthorizeParams(
                clientId = authorizeParams.clientId,
                state = authorizeParams.state,
                responseType = authorizeParams.responseType,
                redirectUri = redirectUri,
            )
        }
    }

    private fun handleUserDataValidationForAuthorizeParams(
        clientId: String,
        state: String,
        responseType: String,
        redirectUri: String
    ): AuthorizeValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> ValidAuthorizeParams.WithoutUserData(
                clientId = clientId,
                state = state,
                responseType = responseType,
                redirectUri = redirectUri,
            )

            else -> ValidAuthorizeParams.WithUserData(
                clientId = clientId,
                state = state,
                responseType = responseType,
                redirectUri = redirectUri,
                userData = userData,
            )
        }
    }

    fun isTokenExchangeParamsValid(tokenExchangeParams: TokenExchangeParams): TokenExchangeValidationResult {
        return when {
            environment.secretGoogleHomeActionsClientId != tokenExchangeParams.clientId -> InvalidTokenExchangeParams
            environment.secretGoogleHomeActionsSecret != tokenExchangeParams.clientSecret -> InvalidTokenExchangeParams
            else -> tokenExchangeParams.handleGrantType()
        }
    }

    private fun TokenExchangeParams.handleGrantType(): TokenExchangeValidationResult =
        when (grantType) {
            TokenExchangeParams.AUTHORIZATION_CODE_TYPE -> handleAuthorizationCode()
            TokenExchangeParams.REFRESH_TOKEN_TYPE -> handleRefreshToken()
            else -> InvalidTokenExchangeParams
        }

    private fun TokenExchangeParams.handleAuthorizationCode(): TokenExchangeValidationResult {
        val redirectUri = GoogleHomeActionsRedirectUrl.create(environment.secretGoogleHomeActionsClientId).value

        return when {
            redirectUri != this.redirectUri -> InvalidTokenExchangeParams
            encryptedTokenValidator.isNotValid(authorizationCode) -> InvalidTokenExchangeParams
            else -> handleUserDataValidationForAuthorizationCode()
        }
    }

    private fun TokenExchangeParams.handleUserDataValidationForAuthorizationCode(): TokenExchangeValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> InvalidTokenExchangeParams
            userData.authorizationCode == null -> InvalidTokenExchangeParams

            timeProvider.currentTimeMillis() > userData.authorizationCode.creationTimeInMillis +
                AUTHORIZATION_CODE_TOKEN_EXPIRATION_IN_MILLIS -> InvalidTokenExchangeParams

            userData.authorizationCode.value != authorizationCode -> InvalidTokenExchangeParams

            else -> ValidAuthorizationCodeType(userData)
        }
    }

    private fun TokenExchangeParams.handleRefreshToken(): TokenExchangeValidationResult {
        return when {
            encryptedTokenValidator.isNotValid(refreshToken) -> InvalidTokenExchangeParams
            else -> handleUserDataValidationForRefreshToken()
        }
    }

    private fun TokenExchangeParams.handleUserDataValidationForRefreshToken(): TokenExchangeValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> InvalidTokenExchangeParams
            userData.refreshToken == null -> InvalidTokenExchangeParams
            userData.refreshToken.value != refreshToken -> InvalidTokenExchangeParams
            else -> ValidRefreshTokenType(userData)
        }
    }

    fun isAccessTokenValid(accessToken: String): AccessTokenValidationResult {
        return when {
            encryptedTokenValidator.isNotValid(accessToken) -> InvalidAccessToken
            else -> handleUserDataValidationForAccessToken(accessToken)
        }
    }

    private fun handleUserDataValidationForAccessToken(accessToken: String): AccessTokenValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> InvalidAccessToken
            userData.accessToken == null -> InvalidAccessToken

            timeProvider.currentTimeMillis() > userData.accessToken.creationTimeInMillis +
                ACCESS_TOKEN_EXPIRATION_IN_MILLIS -> InvalidAccessToken

            userData.accessToken.value != accessToken -> InvalidAccessToken
            else -> ValidAccessToken(userData)
        }
    }

    fun isFulfillmentRequestValid(fulfillmentRequest: FulfillmentRequest): FulfillmentRequestValidationResult {
        return when {
            fulfillmentRequest.requestId.isEmpty() -> InvalidFulfillmentRequest
            else -> fulfillmentRequest.handleUserDataValidationForFulfillmentRequest()
        }
    }

    private fun FulfillmentRequest.handleUserDataValidationForFulfillmentRequest(): FulfillmentRequestValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> InvalidFulfillmentRequest
            else -> input.intent.toValidFulfillmentRequest(requestId, userData)
        }
    }

    private fun Intent.toValidFulfillmentRequest(
        requestId: String,
        userData: UserData
    ): ValidFulfillmentRequest =
        when (this) {
            is Intent.Sync -> ValidFulfillmentRequest.Sync(
                requestId = requestId,
                userData = userData,
            )
        }

    fun isUserSessionValid(userSession: UserSession): Boolean {
        val userData = userDataSource.userData()

        return when {
            userData == null -> false
            userData.sessionToken == null -> false

            timeProvider.currentTimeMillis() > userData.sessionToken.creationTimeInMillis +
                USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS -> false

            userData.sessionToken.value != userSession.sessionToken -> false
            else -> true
        }
    }

    fun isAddDeviceRequestValid(addDeviceRequest: AddDeviceRequest): AddDeviceRequestValidationResult {
        return when {
            roomNameValidator.isNotValid(addDeviceRequest.room) -> AddDeviceRequestValidationResult.InvalidDevice
            deviceNameValidator.isNotValid(addDeviceRequest.deviceName) ->
                AddDeviceRequestValidationResult.InvalidDevice

            else -> addDeviceRequest.handleUserDataValidationForAddDeviceRequest()
        }
    }

    private fun AddDeviceRequest.handleUserDataValidationForAddDeviceRequest(): AddDeviceRequestValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> AddDeviceRequestValidationResult.InvalidDevice
            userData.hasDevice(deviceName) -> AddDeviceRequestValidationResult.InvalidDevice
            else -> AddDeviceRequestValidationResult.ValidDevice(
                userData = userData,
                deviceName = deviceName,
                room = room,
                deviceType = type,
            )
        }
    }

    fun isUpdateDeviceRequestValid(updateDeviceRequest: UpdateDeviceRequest): UpdateDeviceValidationResult {
        val device = updateDeviceRequest.updatedDevice

        return when {
            !device.isDeviceValid() -> UpdateDeviceValidationResult.InvalidDevice
            else -> device.handleDeviceUpdate()
        }
    }

    private fun Device.isDeviceValid(): Boolean =
        when {
            deviceNameValidator.isNotValid(deviceName) -> false
            roomNameValidator.isNotValid(room) -> false
            deviceIdValidator.isNotValid(id.value) -> false
            else -> when (this) {
                is Device.Blind -> isDeviceValid()
            }
        }

    private fun Device.Blind.isDeviceValid(): Boolean = Device.Blind.blindRange.isOnRange(rotation)

    private fun Device.handleDeviceUpdate(): UpdateDeviceValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> UpdateDeviceValidationResult.InvalidDevice
            !userData.canUpdateDevice(this) -> UpdateDeviceValidationResult.InvalidDevice
            else -> UpdateDeviceValidationResult.ValidDevice(
                userData = userData,
                updatedDevice = this,
            )
        }
    }

    sealed interface RequestLoginValidationResult {
        sealed interface ValidRequestLogin : RequestLoginValidationResult {
            val authorizeValidationResult: AuthorizeValidationResult

            data class ValidUser(
                val userData: UserData,
                override val authorizeValidationResult: AuthorizeValidationResult,
            ) : ValidRequestLogin

            data class RegistrableUser(
                val username: String,
                val password: String,
                override val authorizeValidationResult: AuthorizeValidationResult,
            ) : ValidRequestLogin
        }

        data object InvalidRequestLoginParams : RequestLoginValidationResult
    }

    sealed interface AuthorizeValidationResult {
        sealed interface ValidAuthorizeParams : AuthorizeValidationResult {
            val state: String
            val clientId: String
            val redirectUri: String
            val responseType: String

            data class WithUserData(
                override val state: String,
                override val clientId: String,
                override val redirectUri: String,
                override val responseType: String,
                val userData: UserData,
            ) : ValidAuthorizeParams

            data class WithoutUserData(
                override val state: String,
                override val clientId: String,
                override val redirectUri: String,
                override val responseType: String,
            ) : ValidAuthorizeParams
        }

        data object InvalidAuthorizeParams : AuthorizeValidationResult
    }

    sealed interface AccessTokenValidationResult {
        data class ValidAccessToken(
            val userData: UserData,
        ) : AccessTokenValidationResult

        data object InvalidAccessToken : AccessTokenValidationResult
    }

    sealed interface TokenExchangeValidationResult {
        data class ValidAuthorizationCodeType(val userData: UserData) :
            TokenExchangeValidationResult

        data class ValidRefreshTokenType(val userData: UserData) :
            TokenExchangeValidationResult

        data object InvalidTokenExchangeParams : TokenExchangeValidationResult
    }

    sealed interface FulfillmentRequestValidationResult {
        sealed interface ValidFulfillmentRequest : FulfillmentRequestValidationResult {
            val requestId: String
            val userData: UserData

            data class Sync(
                override val requestId: String,
                override val userData: UserData,
            ) : ValidFulfillmentRequest
        }

        data object InvalidFulfillmentRequest : FulfillmentRequestValidationResult
    }

    sealed interface AddDeviceRequestValidationResult {
        data class ValidDevice(
            val userData: UserData,
            val deviceName: String,
            val room: String,
            val deviceType: DeviceType,
        ) : AddDeviceRequestValidationResult

        data object InvalidDevice : AddDeviceRequestValidationResult
    }

    sealed interface UpdateDeviceValidationResult {
        data class ValidDevice(
            val userData: UserData,
            val updatedDevice: Device,
        ) : UpdateDeviceValidationResult

        data object InvalidDevice : UpdateDeviceValidationResult
    }

    companion object {
        const val USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS = 3_600_000L
        const val ACCESS_TOKEN_EXPIRATION_IN_MILLIS = 3_600_000L
        const val AUTHORIZATION_CODE_TOKEN_EXPIRATION_IN_MILLIS = 600_000L
    }
}
