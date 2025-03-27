package theoneclick.client.core.platform

import kotlinx.coroutines.flow.Flow
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType

interface LoggedDataSource {
    fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType,
    ): Flow<AddDeviceResult>

    fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult>

    fun devices(): Flow<DevicesResult>

    fun logout(): Flow<LogoutResult>
}
