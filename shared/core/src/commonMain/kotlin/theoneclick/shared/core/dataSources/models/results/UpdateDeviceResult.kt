package theoneclick.shared.core.dataSources.models.results

sealed interface UpdateDeviceResult {
    data object Success : UpdateDeviceResult

    sealed interface Failure : UpdateDeviceResult {
        data object NotLogged : Failure
        data object UnknownError : Failure
    }
}
