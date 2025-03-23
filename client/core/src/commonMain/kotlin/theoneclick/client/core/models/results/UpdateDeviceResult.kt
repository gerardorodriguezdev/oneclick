package theoneclick.client.core.models.results

sealed interface UpdateDeviceResult {
    data object Success : UpdateDeviceResult
    data object Failure : UpdateDeviceResult
}
