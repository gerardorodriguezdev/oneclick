package oneclick.client.apps.home.dataSources.base

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import oneclick.client.apps.home.dataSources.base.HomeDataSource.SyncDeviceResult
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.requests.SyncDeviceRequest
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

internal interface HomeDataSource {
    suspend fun syncDevice(request: SyncDeviceRequest): SyncDeviceResult

    sealed interface SyncDeviceResult {
        data object Success : SyncDeviceResult
        data object Error : SyncDeviceResult
    }
}

internal class RemoteHomeDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomeDataSource {
    override suspend fun syncDevice(request: SyncDeviceRequest): SyncDeviceResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response = httpClient.post(ClientEndpoint.SYNC_DEVICE.route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> SyncDeviceResult.Success
                    else -> SyncDeviceResult.Error
                }
            } catch (error: Exception) {
                appLogger.e("Exception caught '${error.stackTraceToString()}' while syncing device")
                SyncDeviceResult.Error
            }
        }
}