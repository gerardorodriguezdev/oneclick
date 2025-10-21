package oneclick.client.app.home.dataSources.base

import kotlinx.coroutines.flow.Flow
import oneclick.client.app.home.models.DeviceId
import oneclick.client.app.home.models.DevicePassword

interface DevicesController {
    fun scan(): Flow<DeviceId>
    fun connect(deviceId: DeviceId, devicePassword: DevicePassword): Flow<Char>
}