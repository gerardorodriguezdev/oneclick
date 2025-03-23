package theoneclick.client.core.models.results

sealed interface AddDeviceResult {
    data object Success : AddDeviceResult
    data object Failure : AddDeviceResult
}
