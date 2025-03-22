package theoneclick.client.core.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.dispatchers.platform.DispatchersProvider

interface LoggedDataSource {
    fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType,
    ): Flow<AddDeviceResult>

    fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult>

    fun devices(): Flow<DevicesResult>
}

class RemoteLoggedDataSource(
    private val client: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val idlingResource: IdlingResource,
) : LoggedDataSource {

    override fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType
    ): Flow<AddDeviceResult> =
        flow {
            val response = client.post(ClientEndpoints.ADD_DEVICE.route) {
                contentType(ContentType.Application.Json)
                setBody(
                    AddDeviceRequest(
                        deviceName = deviceName,
                        room = room,
                        type = type,
                    )
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> emit(AddDeviceResult.Success)
                HttpStatusCode.Unauthorized -> emit(AddDeviceResult.Failure.NotLogged)
                else -> emit(AddDeviceResult.Failure.UnknownError)
            }
        }
            .onStart { idlingResource.increment() }
            .onCompletion { idlingResource.decrement() }
            .catch { emit(AddDeviceResult.Failure.UnknownError) }
            .flowOn(dispatchersProvider.io())

    override fun devices(): Flow<DevicesResult> =
        flow {
            val response = client.get(ClientEndpoints.DEVICES.route) {
                contentType(ContentType.Application.Json)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val responseBody =
                        response.body<DevicesResponse>()
                    emit(responseBody.toDevicesResult())
                }
                HttpStatusCode.Unauthorized -> emit(DevicesResult.Failure.NotLogged)
                else -> emit(DevicesResult.Failure.UnknownError)
            }
        }
            .onStart { idlingResource.increment() }
            .onCompletion { idlingResource.decrement() }
            .catch { emit(DevicesResult.Failure.UnknownError) }
            .flowOn(dispatchersProvider.io())

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> =
        flow {
            val response = client.post(ClientEndpoints.UPDATE_DEVICE.route) {
                contentType(ContentType.Application.Json)
                setBody(UpdateDeviceRequest(updatedDevice))
            }

            when (response.status) {
                HttpStatusCode.OK -> emit(UpdateDeviceResult.Success)
                HttpStatusCode.Unauthorized -> emit(UpdateDeviceResult.Failure.NotLogged)
                else -> emit(UpdateDeviceResult.Failure.UnknownError)
            }
        }
            .onStart { idlingResource.increment() }
            .onCompletion { idlingResource.decrement() }
            .catch { emit(UpdateDeviceResult.Failure.UnknownError) }
            .flowOn(dispatchersProvider.io())

    private fun DevicesResponse.toDevicesResult(): DevicesResult =
        DevicesResult.Success(devices = devices)
}
