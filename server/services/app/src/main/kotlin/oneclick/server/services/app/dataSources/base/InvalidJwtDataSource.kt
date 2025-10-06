package oneclick.server.services.app.dataSources.base

import oneclick.shared.contracts.core.models.Uuid

interface InvalidJwtDataSource {
    val jwtExpirationTime: Long

    suspend fun isJwtInvalid(jti: Uuid): Boolean
    suspend fun saveInvalidJwt(jti: Uuid): Boolean
}