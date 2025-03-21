package theoneclick.server.core.data.models

import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.Uuid

@Serializable
data class UserData(
    val userId: Uuid,
    val username: String,
    val hashedPassword: HashedPassword,
    val sessionToken: EncryptedToken? = null,

    val authorizationCode: EncryptedToken? = null,
    val state: String? = null,
    val accessToken: EncryptedToken? = null,
    val refreshToken: EncryptedToken? = null,

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
