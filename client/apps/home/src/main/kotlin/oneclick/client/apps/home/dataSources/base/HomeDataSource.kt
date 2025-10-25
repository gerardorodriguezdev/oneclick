package oneclick.client.apps.home.dataSources.base

import oneclick.shared.contracts.homes.models.requests.SyncDeviceRequest

internal interface HomeDataSource {
    suspend fun syncDevice(request: SyncDeviceRequest): SyncDeviceResult

    sealed interface SyncDeviceResult {
        data object Success : SyncDeviceResult
        data object Error : SyncDeviceResult
    }
}
