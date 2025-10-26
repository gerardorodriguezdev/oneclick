package oneclick.client.apps.home.dataSources

import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Device
import java.util.concurrent.ConcurrentHashMap

internal class MemoryDevicesStore : DevicesStore {
    private val devices = ConcurrentHashMap<Uuid, Device>()

    override fun updateDevice(device: Device) {
        devices[device.id] = device
    }

    override fun getDevices(): List<Device> = devices.values.toList()

    override fun clear() = devices.clear()
}