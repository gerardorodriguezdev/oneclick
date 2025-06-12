package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.LoggedDataSource
import theoneclick.client.features.home.models.results.DevicesResult
import theoneclick.client.features.home.models.results.UpdateDeviceResult
import theoneclick.shared.core.models.entities.Device

internal interface DevicesRepository {
    val devices: SharedFlow<List<Device>>

    fun updateDevice(updatedDevice: Device): Flow<UpdateDeviceResult>

    fun refreshDevices(): Flow<DevicesResult>
}

@Inject
internal class InMemoryDevicesRepository(
    private val loggedDataSource: LoggedDataSource,
) : DevicesRepository {
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    override val devices: StateFlow<List<Device>> = _devices

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
