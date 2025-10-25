package oneclick.client.apps.home.dataSources.base

import kotlinx.coroutines.flow.Flow
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Device

//TODO: Handle error connecting + reconnecting
internal interface DevicesController {
    suspend fun scan(): List<Uuid>
    fun connect(id: Uuid, password: Password): Flow<Device>

    fun authenticatedDevices(): List<AuthenticatedDevice>
    fun reconnect(id: Uuid): Flow<Device>

    fun disconnect(id: Uuid): Boolean
    fun remove(id: Uuid): Boolean

    data class AuthenticatedDevice(
        val id: Uuid,
        val isConnected: Boolean,
    )
}