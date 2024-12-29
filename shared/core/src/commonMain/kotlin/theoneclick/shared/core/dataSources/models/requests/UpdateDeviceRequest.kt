package theoneclick.shared.core.dataSources.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.core.dataSources.models.entities.Device

@Serializable
data class UpdateDeviceRequest(
    val updatedDevice: Device,
)
