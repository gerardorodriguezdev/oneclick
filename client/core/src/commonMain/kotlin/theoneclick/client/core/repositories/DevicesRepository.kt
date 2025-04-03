package theoneclick.client.core.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import theoneclick.client.core.dataSources.LoggedDataSource
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType

//TODO: Test
interface DevicesRepository {
    fun addDevice(
        deviceName: String,
        room: String,
        type: DeviceType,
    ): Flow<AddDeviceResult>

    fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult>

    fun devices(): Flow<DevicesResult>
}

class InMemoryDevicesRepository(
    private val loggedDataSource: LoggedDataSource,
) : DevicesRepository {
    private val devices = mutableListOf<Device>()

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
            .map { result ->
                if (result is AddDeviceResult.Success) {
                    devices.add(result.device)
                }

                result
            }

    override fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult> =
        loggedDataSource
            .updateDevice(updatedDevice)
            .map { result ->
                if (result is UpdateDeviceResult.Success) {
                    val newDevices = devices.mapIndexed { _, device ->
                        if (device.id == updatedDevice.id) {
                            updatedDevice
                        } else {
                            device
                        }
                    }
                    devices.clear()
                    devices.addAll(newDevices)
                }

                result
            }

    override fun devices(): Flow<DevicesResult> =
        loggedDataSource
            .devices()
            .map { result ->
                if (result is DevicesResult.Success) {
                    devices.clear()
                    devices.addAll(result.devices)
                }

                result
            }
}