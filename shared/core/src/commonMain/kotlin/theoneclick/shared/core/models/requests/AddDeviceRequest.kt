package theoneclick.shared.core.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.entities.DeviceType

@Serializable
data class AddDeviceRequest(
    val deviceName: String,
    val room: String,
    val type: DeviceType,
)
