package theoneclick.shared.core.dataSources.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.core.dataSources.models.entities.Device

@Serializable
data class DevicesResponse(
    val devices: List<Device>,
)
