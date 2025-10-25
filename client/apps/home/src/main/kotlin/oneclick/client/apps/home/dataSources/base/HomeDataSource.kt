package oneclick.client.apps.home.dataSources.base

import oneclick.shared.contracts.homes.models.requests.SyncDevicesRequest

internal interface HomeDataSource {
    suspend fun syncDevices(request: SyncDevicesRequest): SyncDevicesResult

    sealed interface SyncDevicesResult {
        data object Success : SyncDevicesResult
        data object Error : SyncDevicesResult
    }
}
