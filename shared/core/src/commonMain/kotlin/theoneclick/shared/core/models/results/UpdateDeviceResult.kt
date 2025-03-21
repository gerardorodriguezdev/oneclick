package theoneclick.shared.core.models.results

sealed interface UpdateDeviceResult {
    data object Success : UpdateDeviceResult

    sealed interface Failure : UpdateDeviceResult {
        data object NotLogged : Failure
        data object UnknownError : Failure
    }
}
