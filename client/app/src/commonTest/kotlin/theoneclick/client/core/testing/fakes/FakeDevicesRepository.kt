package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.client.core.repositories.DevicesRepository
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType

class FakeDevicesRepository(
    var devicesFlow: MutableStateFlow<List<Device>> = MutableStateFlow(emptyList()),
    var addDeviceResultFlow: Flow<AddDeviceResult> = flowOf(),
    var refreshDevicesResultFlow: Flow<DevicesResult> = flowOf(),
    var updateDeviceResultFlow: Flow<UpdateDeviceResult> = flowOf(),
) : DevicesRepository {

    override val devices: SharedFlow<List<Device>>
        get() = devicesFlow

    override fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType
    ): Flow<AddDeviceResult> =
        addDeviceResultFlow

    override fun refreshDevices(): Flow<DevicesResult> = refreshDevicesResultFlow

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> = updateDeviceResultFlow
}
