package theoneclick.client.core.testing.fakes

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.respondJson
import theoneclick.client.core.testing.toRequestBodyObject
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.core.models.responses.RequestLoginResponse
import theoneclick.shared.core.models.responses.UserLoggedResponse

fun fakeHttpClientEngine(
    isUserLogged: () -> Boolean = { false },
    devices: () -> List<Device> = { emptyList() },
    isError: () -> Boolean = { false },
): HttpClientEngine =
    MockEngine { request ->
        val context = Context(
            scope = this,
            request = request,
            isUserLogged = isUserLogged,
            devices = devices,
            isError = isError,
        )

        when (request.url.fullPath) {
            ClientEndpoint.IS_USER_LOGGED.route -> context.handleIsUserLogged()
            ClientEndpoint.REQUEST_LOGIN.route -> context.handleRequestLogin()
            ClientEndpoint.DEVICES.route -> context.handleDevices()
            ClientEndpoint.UPDATE_DEVICE.route -> context.handleUpdateDevice()
            ClientEndpoint.ADD_DEVICE.route -> context.handleAddDevice()
            else -> respondError(HttpStatusCode.NotFound)
        }
    }

private class Context(
    val scope: MockRequestHandleScope,
    val request: HttpRequestData,
    val isUserLogged: () -> Boolean,
    val devices: () -> List<Device>,
    val isError: () -> Boolean,
)

private fun Context.handleIsUserLogged(): HttpResponseData =
    when {
        isError() -> throw Exception("Error")
        !isUserLogged() -> scope.respondJson<UserLoggedResponse>(UserLoggedResponse.NotLogged)
        else -> scope.respondJson<UserLoggedResponse>(UserLoggedResponse.Logged)
    }

private fun Context.handleRequestLogin(): HttpResponseData {
    val requestLoginRequest = request.toRequestBodyObject<RequestLoginRequest>()

    return when {
        isError() -> throw Exception("Error")
        requestLoginRequest == null -> scope.respondError(HttpStatusCode.BadRequest)
        requestLoginRequest.username != TestData.USERNAME -> scope.respondError(HttpStatusCode.BadRequest)
        requestLoginRequest.password != TestData.PASSWORD -> scope.respondError(HttpStatusCode.BadRequest)
        else -> scope.respondJson<RequestLoginResponse>(RequestLoginResponse(TestData.TOKEN))
    }
}

private fun Context.handleDevices(): HttpResponseData =
    when {
        isError() -> throw Exception("Error")
        !isUserLogged() -> scope.respondError(HttpStatusCode.Unauthorized)
        else -> scope.respondJson(DevicesResponse(devices()))
    }

private fun Context.handleAddDevice(): HttpResponseData {
    val addDeviceRequest = request.toRequestBodyObject<AddDeviceRequest>()

    return when {
        isError() -> throw Exception("Error")
        !isUserLogged() -> scope.respondError(HttpStatusCode.Unauthorized)
        addDeviceRequest == null -> scope.respondError(HttpStatusCode.BadRequest)
        addDeviceRequest.deviceName != TestData.DEVICE_NAME -> scope.respondError(HttpStatusCode.BadRequest)
        addDeviceRequest.room != TestData.ROOM_NAME -> scope.respondError(HttpStatusCode.BadRequest)
        else -> scope.respondOk()
    }
}

private fun Context.handleUpdateDevice(): HttpResponseData {
    val updateDeviceRequest = request.toRequestBodyObject<UpdateDeviceRequest>()

    return when {
        isError() -> throw Exception("Error")
        !isUserLogged() -> scope.respondError(HttpStatusCode.Unauthorized)
        updateDeviceRequest == null -> scope.respondError(HttpStatusCode.BadRequest)
        updateDeviceRequest.updatedDevice != TestData.device -> scope.respondError(HttpStatusCode.BadRequest)
        else -> scope.respondOk()
    }
}