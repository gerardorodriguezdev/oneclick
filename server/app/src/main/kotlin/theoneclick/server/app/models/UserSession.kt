package theoneclick.server.app.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val sessionToken: String)
