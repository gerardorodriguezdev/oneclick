package theoneclick.server.app.di

data class Environment(
    val secretSignKey: String,
    val secretEncryptionKey: String,
    val protocol: String,
    val host: String,
    val storageDirectory: String,
    // Optional
    val enableQAAPI: Boolean,
    val disableRateLimit: Boolean,
) {
    val baseUrl: String = "$protocol://$host"
}