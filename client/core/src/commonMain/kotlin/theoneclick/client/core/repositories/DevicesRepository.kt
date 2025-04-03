package theoneclick.client.core.repositories

import kotlinx.coroutines.flow.*
import theoneclick.client.core.dataSources.LoggedDataSource
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType

interface DevicesRepository {
    val devices: SharedFlow<List<Device>>

    fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType,
    ): Flow<AddDeviceResult>

    fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult>

    fun refreshDevices(): Flow<DevicesResult>
}

class InMemoryDevicesRepository(
    private val loggedDataSource: LoggedDataSource,
) : DevicesRepository {
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    override val devices: StateFlow<List<Device>> = _devices

    override fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType
    ): Flow<AddDeviceResult> =
        loggedDataSource
            .addDevice(
                deviceName = deviceName,
                room = room,
                type = type,
            )
            .onEach { result ->
                if (result is AddDeviceResult.Success) {
                    val newDevices = _devices.value + result.device
                    _devices.emit(newDevices)
                }
            }

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> =
        loggedDataSource
            .updateDevice(updatedDevice)
            .onEach { result ->
                val newDevices = _devices.value
                    .mapIndexed { _, device ->
                        if (device.id == updatedDevice.id) {
                            updatedDevice
                        } else {
                            device
                        }
                    }

                _devices.emit(newDevices)
            }

    override fun refreshDevices(): Flow<DevicesResult> =
        loggedDataSource
            .devices()
            .onEach { result ->
                if (result is DevicesResult.Success) {
                    _devices.emit(result.devices)
                }
            }
}