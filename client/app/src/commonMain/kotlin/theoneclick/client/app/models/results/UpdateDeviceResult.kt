package theoneclick.client.app.models.results

sealed interface UpdateDeviceResult {
    data object Success : UpdateDeviceResult
    data object Failure : UpdateDeviceResult
}
