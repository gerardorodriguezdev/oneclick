package oneclick.client.apps.home.dataSources

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import oneclick.client.apps.home.dataSources.base.HomeDataSource
import oneclick.client.apps.home.dataSources.base.HomeDataSource.SyncDevicesResult
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.requests.SyncDevicesRequest
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

internal class RemoteHomeDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomeDataSource {
    override suspend fun syncDevices(request: SyncDevicesRequest): SyncDevicesResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response = httpClient.post(ClientEndpoint.SYNC_DEVICES.route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> SyncDevicesResult.Success
                    else -> SyncDevicesResult.Error
                }
            } catch (error: Exception) {
                appLogger.e("Exception caught '${error.stackTraceToString()}' while syncing devices")
                SyncDevicesResult.Error
            }
        }
}
