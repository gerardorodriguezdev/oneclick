package theoneclick.client.core.models.results

sealed interface AddDeviceResult {
    data object Success : AddDeviceResult

    sealed interface Failure : AddDeviceResult {
        data object NotLogged : Failure
        data object UnknownError : Failure
    }
}
