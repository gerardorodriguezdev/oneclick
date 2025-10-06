package oneclick.server.services.app.plugins.authentication

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.shared.auth.models.JwtUserId.Companion.toJwtUserId
import oneclick.server.shared.auth.security.JwtProvider
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.core.models.Uuid.Companion.toUuid
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun Application.configureAuthentication(
    jwtProvider: JwtProvider,
    logger: Logger,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    install(Authentication) {
        registerJwtSessionsAuthentication(jwtProvider, logger, invalidJwtDataSource)
        registerJwtAuthentication(jwtProvider, invalidJwtDataSource)
    }
}

private fun AuthenticationConfig.registerJwtSessionsAuthentication(
    jwtProvider: JwtProvider,
    logger: Logger,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    session<Jwt>(AuthenticationConstants.JWT_SESSION_AUTHENTICATION) {
        validate { jwt ->
            val decodedJwt = try {
                jwtProvider.jwtVerifier.verify(jwt.value)
            } catch (error: JWTVerificationException) {
                logger.error("Error decoding jwt", error)
                null
            } ?: return@validate null

            val jtiString = decodedJwt.id ?: return@validate null
            val jti = jtiString.toUuid() ?: return@validate null
            if (invalidJwtDataSource.isJwtInvalid(jti)) return@validate null

            val payload = decodedJwt.parsePayload()
            val jwtCredential = JWTCredential(payload)

            jwtProvider.jwtCredentials(jti = jti, jwtCredential = jwtCredential)
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerJwtAuthentication(
    jwtProvider: JwtProvider,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    jwt(AuthenticationConstants.JWT_AUTHENTICATION) {
        realm = jwtProvider.jwtRealm

        verifier(jwtProvider.jwtVerifier)

        validate { jwtCredential ->
            val jtiString = jwtCredential.jwtId ?: return@validate null
            val jti = jtiString.toUuid() ?: return@validate null
            if (invalidJwtDataSource.isJwtInvalid(jti)) return@validate null

            jwtProvider.jwtCredentials(jti = jti, jwtCredential = jwtCredential)
        }

        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun JwtProvider.jwtCredentials(
    jti: Uuid,
    jwtCredential: JWTCredential,
): JwtCredentials? {
    val claim = jwtCredential.payload.getClaim(jwtClaim).asString()
    val jwtUserId = claim.toJwtUserId() ?: return null
    val userId = userId(jwtUserId) ?: return null
    return JwtCredentials(jti = jti, userId = userId)
}

@OptIn(ExperimentalEncodingApi::class)
private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.getDecoder().decode(payload))
    return JWTParser().parsePayload(payloadString)
}
