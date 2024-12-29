package theoneclick.server.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val sessionToken: String)
