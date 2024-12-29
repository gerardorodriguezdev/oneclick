package theoneclick.shared.core.dataSources.models.results

import theoneclick.shared.core.dataSources.models.entities.Device

sealed interface DevicesResult {
    data class Success(val devices: List<Device>) : DevicesResult

    sealed interface Failure : DevicesResult {
        data object NotLogged : Failure
        data object UnknownError : Failure
    }
}
