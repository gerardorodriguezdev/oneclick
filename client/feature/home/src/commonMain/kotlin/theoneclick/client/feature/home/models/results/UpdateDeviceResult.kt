package theoneclick.client.feature.home.models.results

sealed interface UpdateDeviceResult {
    data object Success : UpdateDeviceResult
    data object Failure : UpdateDeviceResult
}
