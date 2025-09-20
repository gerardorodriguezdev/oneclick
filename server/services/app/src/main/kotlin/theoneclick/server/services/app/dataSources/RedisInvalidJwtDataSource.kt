package theoneclick.server.services.app.dataSources

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.withContext
import theoneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.dispatchers.platform.DispatchersProvider

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisInvalidJwtDataSource(
    override val jwtExpirationTime: Long,
    private val syncCommands: RedisCoroutinesCommands<String, String>,
    private val dispatchersProvider: DispatchersProvider,
) : InvalidJwtDataSource {

    override suspend fun isJwtInvalid(jti: Uuid): Boolean =
        withContext(dispatchersProvider.io()) {
            syncCommands.get(jti.value) != null
        }

    override suspend fun saveInvalidJwt(jti: Uuid): Boolean =
        withContext(dispatchersProvider.io()) {
            val reply = syncCommands.setex(jti.value, jwtExpirationTime, jti.value)
            reply != null
        }
}