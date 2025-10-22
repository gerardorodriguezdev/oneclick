package oneclick.client.app.home.dataSources.base

import kotlinx.coroutines.flow.Flow
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.core.models.Uuid

interface DevicesController {
    fun scan(): Flow<Uuid>
    fun connect(id: Uuid, password: Password): Flow<Char>
}