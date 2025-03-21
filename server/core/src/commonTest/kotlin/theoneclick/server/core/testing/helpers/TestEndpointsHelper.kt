package theoneclick.server.core.testing.helpers

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import theoneclick.server.core.models.UserData
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.models.endpoints.ServerEndpoints
import theoneclick.server.core.endpoints.authorize.AuthorizeParams
import theoneclick.server.core.endpoints.authorize.AuthorizeParams.Companion.RESPONSE_TYPE_CODE
import theoneclick.server.core.endpoints.fulfillment.FulfillmentRequest
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.extensions.appendIfNotNull
import theoneclick.shared.core.extensions.urlBuilder
import theoneclick.shared.core.extensions.urlString
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest

object TestEndpointsHelper {

    // Healthz
    suspend fun HttpClient.requestHealthz(): HttpResponse = get(ServerEndpoints.HEALTHZ.route)

    // RequestLogin
    suspend fun HttpClient.requestLogin(
        username: String = TestData.USERNAME,
        password: String = TestData.RAW_PASSWORD,
        authorizeParams: AuthorizeParams? = null,
        userData: UserData? = null,
    ): HttpResponse {
        authorizeParams?.let {
            addAuthorizeParams(authorizeParams)
        }

        userData?.let {
            addUserData(userData)
        }

        return post(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoints.REQUEST_LOGIN.route)
            }
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                RequestLoginRequest(
                    username = username,
                    password = password,
                )
            )
        }
    }

    // Authorize
    suspend fun HttpClient.requestAuthorize(
        clientId: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_CLIENT_ID,
        googleHomeActionsRedirectUrl: String? = TestData.googleHomeActionsRedirectWithClientIdUrl.value,
        state: String? = TestData.state,
        responseType: String? = RESPONSE_TYPE_CODE,
        userSession: UserSession? = TestData.validUserSession,
    ): HttpResponse {
        userSession?.let {
            addUserSession(userSession)
            addUserData(
                TestData.userData.copy(
                    authorizationCode = null,
                    accessToken = null,
                    refreshToken = null,
                    state = null
                )
            )
            addAuthorizeParams(TestData.validAuthorizeParams)
        }

        return get(
            url = authorizeUrlBuilder(
                clientId = clientId,
                googleHomeActionsRedirectUrl = googleHomeActionsRedirectUrl,
                state = state,
                responseType = responseType,
            ).build()
        )
    }

    private fun authorizeUrlBuilder(
        clientId: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_CLIENT_ID,
        googleHomeActionsRedirectUrl: String? = TestData.googleHomeActionsRedirectWithClientIdUrl.value,
        state: String? = TestData.state,
        responseType: String? = RESPONSE_TYPE_CODE,
    ): URLBuilder =
        urlBuilder {
            host = TestData.environment.host
            protocol = URLProtocol.HTTPS
            path(ServerEndpoints.AUTHORIZE.route)

            parameters.appendIfNotNull("state", state)
            parameters.appendIfNotNull("client_id", clientId)
            parameters.appendIfNotNull("redirect_uri", googleHomeActionsRedirectUrl)
            parameters.appendIfNotNull("response_type", responseType)
        }

    fun authorizeUrlString(
        clientId: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_CLIENT_ID,
        googleHomeActionsRedirectUrl: String? = TestData.googleHomeActionsRedirectWithClientIdUrl.value,
        state: String? = TestData.state,
        responseType: String? = RESPONSE_TYPE_CODE,
    ): String = authorizeUrlBuilder(
        clientId = clientId,
        googleHomeActionsRedirectUrl = googleHomeActionsRedirectUrl,
        state = state,
        responseType = responseType,
    ).buildString()

    // TokenExchange
    @Suppress("LongParameterList")
    suspend fun HttpClient.requestTokenExchange(
        clientId: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_CLIENT_ID,
        clientSecret: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_SECRET,
        grantTypeString: String?,
        authorizationCode: String? = null,
        refreshToken: String? = null,
        redirectUrl: String? = null,
        userSession: UserSession? = TestData.validUserSession,
        userData: UserData?,
    ): HttpResponse {
        userSession?.let {
            addUserSession(userSession)
        }

        userData?.let {
            addUserData(userData)
        }

        return submitForm(
            url = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ServerEndpoints.TOKEN_EXCHANGE.route)
            },
            formParameters = Parameters.build {
                appendIfNotNull("client_id", clientId)
                appendIfNotNull("client_secret", clientSecret)
                appendIfNotNull("grant_type", grantTypeString)
                appendIfNotNull("code", authorizationCode)
                appendIfNotNull("refresh_token", refreshToken)
                appendIfNotNull("redirect_uri", redirectUrl)
            }
        )
    }

    // Fulfillment
    suspend fun HttpClient.requestFulfillment(
        bearer: String? = TestData.ENCRYPTED_TOKEN_VALUE,
        fulfillmentRequest: FulfillmentRequest,
    ): HttpResponse {
        bearer?.let {
            addUserData(
                TestData.userData.copy(
                    accessToken = TestData.encryptedToken.copy(
                        value = bearer,
                    )
                )
            )
        }

        return post(
            urlString = urlString {
                host = TestData.environment.host
                protocol = URLProtocol.HTTPS
                path(ServerEndpoints.FULFILLMENT.route)
            }
        ) {
            contentType(ContentType.Application.Json)
            setBody(fulfillmentRequest)

            bearer?.let {
                headers.append(HttpHeaders.Authorization, "Bearer $bearer")
            }
        }
    }

    // AddDevice
    suspend fun HttpClient.requestAddDevice(
        deviceName: String = TestData.DEVICE_NAME,
        room: String = TestData.ROOM,
        type: DeviceType = DeviceType.BLIND,
        userSession: UserSession? = TestData.validUserSession,
    ): HttpResponse {
        userSession?.let {
            addUserSession(userSession)
            addUserData(
                TestData.userData.copy(
                    authorizationCode = null,
                    accessToken = null,
                    refreshToken = null,
                    state = null,
                    devices = emptyList(),
                )
            )
        }

        return post(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoints.ADD_DEVICE.route)
            },
            block = {
                contentType(ContentType.Application.Json)
                setBody(
                    AddDeviceRequest(
                        deviceName = deviceName,
                        room = room,
                        type = type,
                    )
                )
            }
        )
    }

    // Devices
    suspend fun HttpClient.requestDevices(
        devices: List<Device> = TestData.devices,
        userSession: UserSession? = TestData.validUserSession,
    ): HttpResponse {
        userSession?.let {
            addUserSession(userSession)
            addUserData(
                TestData.userData.copy(
                    authorizationCode = null,
                    accessToken = null,
                    refreshToken = null,
                    state = null,
                    devices = devices,
                )
            )
        }

        return get(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoints.DEVICES.route)
            },
            block = {
                contentType(ContentType.Application.Json)
            },
        )
    }

    // UpdateDevice
    suspend fun HttpClient.requestUpdateDevice(
        updatedDevice: Device,
        devices: List<Device> = TestData.devices,
        userSession: UserSession? = TestData.validUserSession,
    ): HttpResponse {
        userSession?.let {
            addUserSession(userSession)
            addUserData(
                TestData.userData.copy(
                    authorizationCode = null,
                    accessToken = null,
                    refreshToken = null,
                    state = null,
                    devices = devices,
                )
            )
        }

        return post(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoints.UPDATE_DEVICE.route)
            },
            block = {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateDeviceRequest(updatedDevice)
                )
            }
        )
    }

    // IsUserLogged
    suspend fun HttpClient.requestIsUserLogged(
        userSession: UserSession? = TestData.validUserSession,
    ): HttpResponse {
        userSession?.let {
            addUserSession(userSession)
            addUserData(
                TestData.userData.copy(
                    authorizationCode = null,
                    accessToken = null,
                    refreshToken = null,
                    state = null,
                    devices = listOf(),
                )
            )
        }

        return get(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoints.IS_USER_LOGGED.route)
            }
        )
    }

    // QAAPI
    private suspend fun HttpClient.addUserSession(userSession: UserSession): HttpResponse =
        post(ServerEndpoints.ADD_USER_SESSION.route) {
            contentType(ContentType.Application.Json)
            setBody(userSession)
        }

    private suspend fun HttpClient.addAuthorizeParams(authorizeParams: AuthorizeParams): HttpResponse =
        post(ServerEndpoints.ADD_AUTHORIZE_REDIRECT.route) {
            contentType(ContentType.Application.Json)
            setBody(authorizeParams)
        }

    private suspend fun HttpClient.addUserData(userData: UserData): HttpResponse =
        post(ServerEndpoints.ADD_USER_DATA.route) {
            contentType(ContentType.Application.Json)
            setBody(userData)
        }
}
