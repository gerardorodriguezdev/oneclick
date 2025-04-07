package theoneclick.client.core.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.models.responses.AddDeviceResponse
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.core.platform.AppLogger
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
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : LoggedDataSource {

    override fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType
    ): Flow<AddDeviceResult> =
        flow {
            val response = httpClient.post(ClientEndpoint.ADD_DEVICE.route) {
                setBody(
                    AddDeviceRequest(
                        deviceName = deviceName,
                        room = room,
                        type = type,
                    )
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val responseBody = response.body<AddDeviceResponse>()
                    emit(AddDeviceResult.Success(responseBody.device))
                }

                else -> emit(AddDeviceResult.Failure)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while adding device '$deviceName'")

                emit(AddDeviceResult.Failure)
            }
            .flowOn(dispatchersProvider.io())

    override fun devices(): Flow<DevicesResult> =
        flow {
            val response = httpClient.get(ClientEndpoint.DEVICES.route)

            when (response.status) {
                HttpStatusCode.OK -> {
                    val responseBody = response.body<DevicesResponse>()
                    emit(DevicesResult.Success(devices = responseBody.devices))
                }

                else -> emit(DevicesResult.Failure)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while getting devices ")
                emit(DevicesResult.Failure)
            }
            .flowOn(dispatchersProvider.io())

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> =
        flow {
            val response = httpClient.post(ClientEndpoint.UPDATE_DEVICE.route) {
                setBody(UpdateDeviceRequest(updatedDevice))
            }

            when (response.status) {
                HttpStatusCode.OK -> emit(UpdateDeviceResult.Success)
                else -> emit(UpdateDeviceResult.Failure)
            }
        }
            .catch { exception ->
                appLogger.e(
                    "Exception catched '${exception.stackTraceToString()}' while updating device '$updatedDevice'"
                )
                emit(UpdateDeviceResult.Failure)
            }
            .flowOn(dispatchersProvider.io())
}
