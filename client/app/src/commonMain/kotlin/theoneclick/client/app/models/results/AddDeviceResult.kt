package theoneclick.client.app.models.results

import theoneclick.shared.core.models.entities.Device

sealed interface AddDeviceResult {
    data class Success(val device: Device) : AddDeviceResult
    data object Failure : AddDeviceResult
}
