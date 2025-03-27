package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.platform.LoggedDataSource
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult

class FakeLoggedDataSource(
    var addDeviceResultFlow: Flow<AddDeviceResult> = flowOf(),
    var devicesResultFlow: Flow<DevicesResult> = flowOf(),
    var updateDeviceResultFlow: Flow<UpdateDeviceResult> = flowOf(),
) : LoggedDataSource {

    override fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType
    ): Flow<AddDeviceResult> =
        addDeviceResultFlow

    override fun devices(): Flow<DevicesResult> = devicesResultFlow

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> = updateDeviceResultFlow
}
