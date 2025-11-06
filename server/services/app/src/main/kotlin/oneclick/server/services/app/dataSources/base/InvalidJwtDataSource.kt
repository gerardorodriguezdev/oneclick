package oneclick.server.services.app.dataSources.base

import oneclick.server.services.app.plugins.authentication.JwtCredentials
import oneclick.shared.contracts.core.models.Uuid

internal interface InvalidJwtDataSource {
    suspend fun isJwtInvalid(jti: Uuid): Boolean
    suspend fun saveInvalidJwt(jwtCredentials: JwtCredentials): Boolean
}