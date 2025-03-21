package theoneclick.shared.core.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.entities.Device

@Serializable
data class UpdateDeviceRequest(
    val updatedDevice: Device,
)
