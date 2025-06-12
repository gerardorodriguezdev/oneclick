package theoneclick.client.features.home.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.models.results.DevicesResult
import theoneclick.client.features.home.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

internal interface LoggedDataSource {
    fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult>

    fun devices(): Flow<DevicesResult>
}

@Inject
internal class RemoteLoggedDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : LoggedDataSource {

    override fun devices(): Flow<DevicesResult> =
        flow {
            val response = httpClient.get(ClientEndpoint.DEVICES.route)

            when (response.status) {
                HttpStatusCode.OK -> {
                    val responseBody = response.body<DevicesResponse>()
                    emit(DevicesResult.Success(devices = responseBody.devices))
                }

                else -> emit(DevicesResult.Error)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while getting devices ")
                emit(DevicesResult.Error)
            }
            .flowOn(dispatchersProvider.io())

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> =
        flow {
            val response = httpClient.post(ClientEndpoint.UPDATE_DEVICE.route) {
                setBody(UpdateDeviceRequest(updatedDevice))
            }

            when (response.status) {
                HttpStatusCode.OK -> emit(UpdateDeviceResult.Success)
                else -> emit(UpdateDeviceResult.Error)
            }
        }
            .catch { exception ->
                appLogger.e(
                    "Exception catched '${exception.stackTraceToString()}' while updating device '$updatedDevice'"
                )
                emit(UpdateDeviceResult.Error)
            }
            .flowOn(dispatchersProvider.io())
}
