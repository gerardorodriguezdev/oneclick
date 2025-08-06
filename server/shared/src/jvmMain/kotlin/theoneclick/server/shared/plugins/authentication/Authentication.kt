package theoneclick.server.shared.plugins.authentication

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import theoneclick.server.shared.di.Environment
import theoneclick.server.shared.models.JwtPayload
import theoneclick.server.shared.security.Encryptor
import theoneclick.shared.contracts.core.models.Jwt
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun Application.configureAuthentication(environment: Environment, encryptor: Encryptor) {
    install(Authentication) {
        registerJwtSessionsAuthentication(encryptor)
        registerJwtAuthentication(environment, encryptor)
    }
}

private fun AuthenticationConfig.registerJwtSessionsAuthentication(encryptor: Encryptor) {
    session<Jwt>(AuthenticationConstants.JWT_SESSION_AUTHENTICATION) {
        validate { jwt ->
            val jWTVerifier = encryptor.jwtVerifier
            val decodedJwt = try {
                jWTVerifier.verify(jwt.value)
            } catch (_: JWTVerificationException) {
                null
            } ?: return@validate null
            val payload = decodedJwt.parsePayload()
            val credential = JWTCredential(payload)
            credential.toJwtPayload(encryptor)
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerJwtAuthentication(
    environment: Environment,
    encryptor: Encryptor,
) {
    jwt(AuthenticationConstants.JWT_AUTHENTICATION) {
        realm = environment.jwtRealm

        verifier(encryptor.jwtVerifier)

        validate { credential -> credential.toJwtPayload(encryptor) }

        challenge { defaultScheme, realm ->
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun JWTCredential.toJwtPayload(encryptor: Encryptor): JwtPayload? {
    val jwtPayloadString =
        payload.getClaim(AuthenticationConstants.JWT_PAYLOAD_CLAIM_NAME).asString()
    return encryptor.jwtPayload(jwtPayloadString).getOrNull()
}

@OptIn(ExperimentalEncodingApi::class)
private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.decode(payload))
    return JWTParser().parsePayload(payloadString)
}