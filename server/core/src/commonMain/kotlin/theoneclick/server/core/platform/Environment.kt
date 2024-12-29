package theoneclick.server.core.platform

data class Environment(
    val secretGoogleHomeActionsClientId: String,
    val secretGoogleHomeActionsSecret: String,
    val secretSignKey: String,
    val secretEncryptionKey: String,
    val host: String,
    // Optional
    val enableQAAPI: Boolean,
    val disableRateLimit: Boolean,
)
