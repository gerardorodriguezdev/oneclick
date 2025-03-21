package theoneclick.server.core.models

import io.ktor.http.*
import kotlinx.serialization.Serializable
import theoneclick.shared.core.extensions.urlBuilder

@Serializable
@JvmInline
value class GoogleHomeActionsRedirectUrl private constructor(val value: String) {

    companion object {
        fun create(clientId: String): GoogleHomeActionsRedirectUrl =
            GoogleHomeActionsRedirectUrl(
                urlWithPath()
                    .apply {
                        appendPathSegments(clientId)
                    }.buildString()
            )

        fun create(
            authorizationCode: String,
            clientId: String,
            state: String,
        ): GoogleHomeActionsRedirectUrl =
            GoogleHomeActionsRedirectUrl(
                urlWithPath()
                    .apply {
                        appendPathSegments(clientId)
                        parameters.append("code", authorizationCode)
                        parameters.append("state", state)
                    }.buildString()
            )

        fun url(): URLBuilder = urlBuilder {
            protocol = URLProtocol.HTTPS
            host = "oauth-redirect.googleusercontent.com"
        }

        private fun urlWithPath(): URLBuilder =
            url()
                .apply {
                    path("r")
                }
    }
}
