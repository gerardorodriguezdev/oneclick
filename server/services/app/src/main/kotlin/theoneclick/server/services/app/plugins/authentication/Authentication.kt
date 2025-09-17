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
import theoneclick.server.shared.auth.models.JwtPayload
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.shared.contracts.auth.models.Jwt
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun Application.configureAuthentication(jwtProvider: JwtProvider, logger: Logger) {
    install(Authentication) {
        registerJwtSessionsAuthentication(jwtProvider, logger)
        registerJwtAuthentication(jwtProvider)
    }
}

private fun AuthenticationConfig.registerJwtSessionsAuthentication(jwtProvider: JwtProvider, logger: Logger) {
    session<Jwt>(AuthenticationConstants.JWT_SESSION_AUTHENTICATION) {
        validate { jwt ->
            val decodedJwt = try {
                jwtProvider.jwtVerifier.verify(jwt.value)
            } catch (error: JWTVerificationException) {
                logger.error("Error decoding jwt", error)
                null
            } ?: return@validate null
            val payload = decodedJwt.parsePayload()
            val credential = JWTCredential(payload)
            credential.toJwtPayload(jwtProvider)
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerJwtAuthentication(jwtProvider: JwtProvider) {
    jwt(AuthenticationConstants.JWT_AUTHENTICATION) {
        realm = jwtProvider.jwtRealm

        verifier(jwtProvider.jwtVerifier)

        validate { credential -> credential.toJwtPayload(jwtProvider) }

        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun JWTCredential.toJwtPayload(jwtProvider: JwtProvider): JwtPayload? {
    val jwtPayloadString =
        payload.getClaim(jwtProvider.jwtClaim).asString()
    return jwtProvider.jwtPayload(jwtPayloadString).getOrNull()
}

@OptIn(ExperimentalEncodingApi::class)
private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.decode(payload))
    return JWTParser().parsePayload(payloadString)
}

internal object AuthenticationConstants {
    const val JWT_SESSION_AUTHENTICATION = "jwt_session_authentication"
    const val JWT_AUTHENTICATION = "jwt_authentication"
}
