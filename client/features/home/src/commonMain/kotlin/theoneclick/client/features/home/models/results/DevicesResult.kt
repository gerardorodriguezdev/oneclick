package theoneclick.client.features.home.models.results

import theoneclick.shared.core.models.entities.Device

internal sealed interface DevicesResult {
    data class Success(val devices: List<Device>) : DevicesResult
    data object Error : DevicesResult
}
