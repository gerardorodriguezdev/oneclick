package oneclick.shared.contracts.homes.models.requests

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.homes.models.Device

@Serializable
data class SyncDeviceRequest(
    val device: Device,
)