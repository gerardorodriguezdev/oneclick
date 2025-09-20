package theoneclick.server.services.app.dataSources.base

import theoneclick.shared.contracts.core.models.Uuid

interface InvalidJwtDataSource {
    val jwtExpirationTime: Long

    suspend fun isJwtInvalid(jti: Uuid): Boolean
    suspend fun saveInvalidJwt(jti: Uuid): Boolean
}