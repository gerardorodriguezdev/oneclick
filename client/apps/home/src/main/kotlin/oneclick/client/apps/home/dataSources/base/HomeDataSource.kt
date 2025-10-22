package oneclick.client.apps.home.dataSources.base

import oneclick.shared.contracts.homes.models.Device

interface HomeDataSource {
    suspend fun saveDevice(device: Device): SaveDeviceResult

    sealed interface SaveDeviceResult {
        data object Success : SaveDeviceResult
        data object Failure : SaveDeviceResult
    }
}