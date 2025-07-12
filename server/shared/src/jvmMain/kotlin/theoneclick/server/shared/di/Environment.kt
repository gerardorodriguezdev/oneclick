package theoneclick.server.shared.di

data class Environment(
    val secretSignKey: String,
    val secretEncryptionKey: String,
    val protocol: String,
    val host: String,
    val jdbcUrl: String,
    val postgresUsername: String,
    val postgresPassword: String,
    val redisUrl: String,
    val redisUsername: String,
    val redisPassword: String,
    // Optional
    val enableQAAPI: Boolean,
    val disableRateLimit: Boolean,
    val useMemoryDatabases: Boolean,
) {
    val baseUrl: String = "$protocol://$host"
}
