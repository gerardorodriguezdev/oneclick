package oneclick.client.apps.home.dataSources.base

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import oneclick.client.apps.home.dataSources.base.HomeDataSource.SaveDeviceResult
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.requests.SaveDeviceRequest
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

internal interface HomeDataSource {
    suspend fun saveDevice(request: SaveDeviceRequest): SaveDeviceResult

    sealed interface SaveDeviceResult {
        data object Success : SaveDeviceResult
        data object Error : SaveDeviceResult
    }
}

internal class RemoteHomeDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomeDataSource {
    override suspend fun saveDevice(request: SaveDeviceRequest): SaveDeviceResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response = httpClient.post(ClientEndpoint.SAVE_DEVICE.route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> SaveDeviceResult.Success
                    else -> SaveDeviceResult.Error
                }
            } catch (error: Exception) {
                appLogger.e("Exception caught '${error.stackTraceToString()}' while saving device")
                SaveDeviceResult.Error
            }
        }
}