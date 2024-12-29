package theoneclick.shared.core.dataSources.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.core.dataSources.models.entities.DeviceType

@Serializable
data class AddDeviceRequest(
    val deviceName: String,
    val room: String,
    val type: DeviceType,
)
