package theoneclick.client.core.models.results

import theoneclick.shared.core.models.entities.Device

sealed interface DevicesResult {
    data class Success(val devices: List<Device>) : DevicesResult
    data object Failure : DevicesResult
}
