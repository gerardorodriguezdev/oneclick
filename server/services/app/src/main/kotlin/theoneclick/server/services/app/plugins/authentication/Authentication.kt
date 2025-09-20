package theoneclick.server.services.app.plugins.authentication

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
import kotlinx.serialization.Serializable
import theoneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.shared.contracts.auth.models.Jwt
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.core.models.Uuid.Companion.toUuid
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun Application.configureAuthentication(
    jwtProvider: JwtProvider,
    logger: Logger,
    invalidJwtDataSource: InvalidJwtDataSource,
    encryptor: Encryptor,
) {
    install(Authentication) {
        registerJwtSessionsAuthentication(jwtProvider, logger, invalidJwtDataSource, encryptor)
        registerJwtAuthentication(jwtProvider, invalidJwtDataSource, encryptor)
    }
}

private fun AuthenticationConfig.registerJwtSessionsAuthentication(
    jwtProvider: JwtProvider,
    logger: Logger,
    invalidJwtDataSource: InvalidJwtDataSource,
    encryptor: Encryptor,
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

            authCredentials(jti = jti, jwtCredential = jwtCredential, jwtProvider = jwtProvider, encryptor = encryptor)
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerJwtAuthentication(
    jwtProvider: JwtProvider,
    invalidJwtDataSource: InvalidJwtDataSource,
    encryptor: Encryptor,
) {
    jwt(AuthenticationConstants.JWT_AUTHENTICATION) {
        realm = jwtProvider.jwtRealm

        verifier(jwtProvider.jwtVerifier)

        validate { jwtCredential ->
            val jtiString = jwtCredential.jwtId ?: return@validate null
            val jti = jtiString.toUuid() ?: return@validate null
            if (invalidJwtDataSource.isJwtInvalid(jti)) return@validate null

            authCredentials(jti = jti, jwtCredential = jwtCredential, jwtProvider = jwtProvider, encryptor = encryptor)
        }

        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun authCredentials(
    jti: Uuid,
    jwtCredential: JWTCredential,
    jwtProvider: JwtProvider,
    encryptor: Encryptor
): AuthCredentials? {
    val userIdString = jwtCredential.payload.getClaim(jwtProvider.jwtClaim).asString()
    val decodedUserIdString = Base64.getDecoder().decode(userIdString)
    val decryptedUserIdString = encryptor.decrypt(decodedUserIdString).getOrThrow()
    val userId = decryptedUserIdString.toUuid() ?: return null
    return AuthCredentials(jti = jti, userId = userId)
}

@Serializable
class AuthCredentials(
    val jti: Uuid,
    val userId: Uuid,
)

@OptIn(ExperimentalEncodingApi::class)
private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.getDecoder().decode(payload))
    return JWTParser().parsePayload(payloadString)
}

internal object AuthenticationConstants {
    const val JWT_SESSION_AUTHENTICATION = "jwt_session_authentication"
    const val JWT_AUTHENTICATION = "jwt_authentication"
}
