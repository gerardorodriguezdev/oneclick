package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Device
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
data class User(
    val id: Uuid,
    val username: Username,
    val hashedPassword: HashedPassword,
    val sessionToken: EncryptedToken? = null,

    val devices: List<Device> = emptyList(),
) {
    fun hasDevice(deviceName: String): Boolean =
        devices.any { device -> device.deviceName == deviceName }

    fun device(deviceId: Uuid): Device? = devices.firstOrNull { device -> device.id.value == deviceId.value }

    fun canUpdateDevice(updatedDevice: Device): Boolean {
        val currentDevice = devices.firstOrNull { device -> device.id.value == updatedDevice.id.value }
        return currentDevice != null && currentDevice::class == updatedDevice::class
    }
}
