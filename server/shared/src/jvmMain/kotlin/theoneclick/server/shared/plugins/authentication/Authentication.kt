package theoneclick.server.shared.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import theoneclick.server.shared.di.Environment
import theoneclick.server.shared.security.Encryptor

fun Application.configureAuthentication(environment: Environment, encryptor: Encryptor) {
    install(Authentication) {
        registerJwtAuthentication(environment, encryptor)
    }
}

private fun AuthenticationConfig.registerJwtAuthentication(
    environment: Environment,
    encryptor: Encryptor,
) {
    jwt(AuthenticationConstants.JWT_AUTHENTICATION) {
        realm = environment.jwtRealm
        verifier(encryptor.jwtVerifier())

        validate { credential ->
            val jwtPayloadString =
                credential.payload.getClaim(AuthenticationConstants.JWT_PAYLOAD_CLAIM_NAME).asString()
            encryptor.jwtPayload(jwtPayloadString).getOrNull()
        }

        challenge { defaultScheme, realm ->
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
