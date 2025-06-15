package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import theoneclick.server.app.di.Environment
import theoneclick.server.app.plugins.authentication.AuthenticationConstants.COOKIE_SESSION_DURATION_IN_SECONDS
import theoneclick.server.app.plugins.authentication.AuthenticationConstants.USER_SESSION
import theoneclick.server.app.security.IvGenerator
import theoneclick.shared.contracts.core.dtos.TokenDto

fun Application.configureSessions() {
    val environment: Environment by inject()
    val ivGenerator: IvGenerator by inject()
    val sessionTransformer = sessionTransformer(environment, ivGenerator)

    install(Sessions) {
        cookie<TokenDto>(USER_SESSION) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = COOKIE_SESSION_DURATION_IN_SECONDS
            cookie.secure = true
            cookie.httpOnly = true
            serializer = sessionSerializer()
            transform(sessionTransformer)
        }
    }
}

private fun sessionTransformer(environment: Environment, ivGenerator: IvGenerator): SessionTransportTransformer =
    SessionTransportTransformerEncrypt(
        signKey = hex(environment.secretSignKey),
        encryptionKey = hex(environment.secretEncryptionKey),
        ivGenerator = ivGenerator::iv,
    )

private inline fun <reified T> sessionSerializer(): SessionSerializer<T> =
    object : SessionSerializer<T> {
        override fun serialize(session: T): String = Json.encodeToString(session)

        override fun deserialize(text: String): T = Json.decodeFromString(text)
    }
