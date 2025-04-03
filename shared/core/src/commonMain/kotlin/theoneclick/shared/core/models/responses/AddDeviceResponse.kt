package theoneclick.shared.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.entities.Device

@Serializable
data class AddDeviceResponse(
    val device: Device,
)
