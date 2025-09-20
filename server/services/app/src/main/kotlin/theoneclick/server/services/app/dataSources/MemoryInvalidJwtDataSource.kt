package theoneclick.server.services.app.dataSources

import theoneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.timeProvider.TimeProvider
import java.util.concurrent.ConcurrentHashMap

class MemoryInvalidJwtDataSource(
    override val jwtExpirationTime: Long,
    private val timeProvider: TimeProvider,
) : InvalidJwtDataSource {
    private val invalidJwts = ConcurrentHashMap<Uuid, Long>()

    override suspend fun isJwtInvalid(jti: Uuid): Boolean {
        val expirationTime = invalidJwts[jti] ?: return false
        val isExpired = expirationTime < timeProvider.currentTimeMillis()
        if (isExpired) invalidJwts.remove(jti)
        return isExpired
    }

    override suspend fun saveInvalidJwt(jti: Uuid): Boolean {
        invalidJwts[jti] = timeProvider.currentTimeMillis() + jwtExpirationTime
        return true
    }
}