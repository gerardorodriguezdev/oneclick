package theoneclick.server.core.platform

data class Environment(
    val secretSignKey: String,
    val secretEncryptionKey: String,
    val host: String,
    // Optional
    val enableQAAPI: Boolean,
    val disableRateLimit: Boolean,
)
