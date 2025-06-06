package theoneclick.client.feature.home.models.results

import theoneclick.shared.core.models.entities.Device

internal sealed interface AddDeviceResult {
    data class Success(val device: Device) : AddDeviceResult
    data object Failure : AddDeviceResult
}
