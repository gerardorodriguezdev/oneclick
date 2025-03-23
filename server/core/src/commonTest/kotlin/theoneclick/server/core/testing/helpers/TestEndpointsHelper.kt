package theoneclick.server.core.testing.helpers

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import theoneclick.server.core.extensions.urlString
import theoneclick.server.core.models.UserData
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.models.endpoints.ServerEndpoint
import theoneclick.server.core.testing.TestData
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest

object TestEndpointsHelper {

    // Healthz
    suspend fun HttpClient.requestHealthz(): HttpResponse = get(ServerEndpoint.HEALTHZ.route)

    // RequestLogin
    suspend fun HttpClient.requestLogin(
        username: String = TestData.USERNAME,
        password: String = TestData.RAW_PASSWORD,
        userData: UserData? = null,
    ): HttpResponse {
        userData?.let {
            addUserData(userData)
        }

        return post(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoint.REQUEST_LOGIN.route)
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
                TestData.userData.copy(devices = emptyList())
            )
        }

        return post(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoint.ADD_DEVICE.route)
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
                TestData.userData.copy(devices = devices)
            )
        }

        return get(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoint.DEVICES.route)
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
                TestData.userData.copy(devices = devices)
            )
        }

        return post(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoint.UPDATE_DEVICE.route)
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
                TestData.userData.copy(devices = listOf())
            )
        }

        return get(
            urlString = urlString {
                protocol = URLProtocol.HTTPS
                host = TestData.environment.host
                path(ClientEndpoint.IS_USER_LOGGED.route)
            }
        )
    }

    // QAAPI
    private suspend fun HttpClient.addUserSession(userSession: UserSession): HttpResponse =
        post(ServerEndpoint.ADD_USER_SESSION.route) {
            contentType(ContentType.Application.Json)
            setBody(userSession)
        }

    private suspend fun HttpClient.addUserData(userData: UserData): HttpResponse =
        post(ServerEndpoint.ADD_USER_DATA.route) {
            contentType(ContentType.Application.Json)
            setBody(userData)
        }
}
