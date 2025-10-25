package oneclick.client.apps.home.dataSources.base

import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.core.models.Uuid

//TODO: Internally manage connections + update on devices store
internal interface DevicesController {
    suspend fun scan(): List<Uuid>
    suspend fun connect(id: Uuid, password: Password): Boolean
    suspend fun disconnect(id: Uuid): Boolean
    suspend fun remove(id: Uuid): Boolean
    suspend fun reconnect(id: Uuid): Boolean
    suspend fun authenticatedDevices(): List<AuthenticatedDevice>

    data class AuthenticatedDevice(
        val id: Uuid,
        val isConnected: Boolean,
    )
}