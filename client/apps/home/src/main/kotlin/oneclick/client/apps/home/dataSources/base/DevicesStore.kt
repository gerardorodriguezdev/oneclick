package oneclick.client.apps.home.dataSources.base

import oneclick.shared.contracts.homes.models.Device

internal interface DevicesStore {
    fun updateDevice(device: Device)
    fun getDevices(): List<Device>
    fun clear()
}