package theoneclick.server.app

import theoneclick.server.app.di.Environment
import theoneclick.server.app.di.base.JvmDependencies
import theoneclick.server.app.entrypoint.server

fun main() {
    server(
        dependencies = JvmDependencies(
            environment = Environment(
                secretSignKey = System.getenv("SECRET_SIGN_KEY"),
                secretEncryptionKey = System.getenv("SECRET_ENCRYPTION_KEY"),
                host = System.getenv("HOST"),
                storageDirectory = System.getenv("STORAGE_DIRECTORY"),
                enableQAAPI = System.getenv("ENABLE_QAAPI") == "true",
                disableRateLimit = System.getenv("DISABLE_RATE_LIMIT") == "true",
            ),
        )
    ).start(wait = true)
}
