package theoneclick.server.core.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val sessionToken: String)
