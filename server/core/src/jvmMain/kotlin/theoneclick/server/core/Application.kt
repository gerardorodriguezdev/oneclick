package theoneclick.server.core

import theoneclick.server.core.entrypoint.server
import theoneclick.server.core.models.Path
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.platform.JvmDependencies
import theoneclick.server.core.platform.PathProvider

fun main() {
    server(
        dependencies = JvmDependencies(
            environment = Environment(
                secretSignKey = System.getenv("JVM_SECRET_SIGN_KEY"),
                secretEncryptionKey = System.getenv("JVM_SECRET_ENCRYPTION_KEY"),
                host = System.getenv("JVM_HOST"),
                enableQAAPI = System.getenv("JVM_ENABLE_QAAPI") == "true",
                disableRateLimit = System.getenv("JVM_DISABLE_RATE_LIMIT") == "true",
            ),
            directory = Path(PathProvider.DIRECTORY_NAME),
        )
    ).start(wait = true)
}
