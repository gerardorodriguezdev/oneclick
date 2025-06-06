package theoneclick.client.features.home.models.results

internal sealed interface UpdateDeviceResult {
    data object Success : UpdateDeviceResult
    data object Error : UpdateDeviceResult
}
