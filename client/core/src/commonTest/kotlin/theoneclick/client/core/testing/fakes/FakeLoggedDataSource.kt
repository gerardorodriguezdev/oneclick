package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import theoneclick.shared.core.dataSources.LoggedDataSource
import theoneclick.shared.core.dataSources.models.entities.Device
import theoneclick.shared.core.dataSources.models.entities.DeviceType
import theoneclick.shared.core.dataSources.models.results.AddDeviceResult
import theoneclick.shared.core.dataSources.models.results.DevicesResult
import theoneclick.shared.core.dataSources.models.results.UpdateDeviceResult

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
