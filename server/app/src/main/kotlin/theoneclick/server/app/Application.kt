package theoneclick.server.app

import theoneclick.server.app.entrypoint.server
import theoneclick.server.app.models.Path
import theoneclick.server.app.platform.Environment
import theoneclick.server.app.platform.PathProvider
import theoneclick.server.app.platform.base.JvmDependencies

fun main() {
    server(
        dependencies = JvmDependencies(
            environment = Environment(
                secretSignKey = System.getenv("SECRET_SIGN_KEY"),
                secretEncryptionKey = System.getenv("SECRET_ENCRYPTION_KEY"),
                host = System.getenv("HOST"),
                enableQAAPI = System.getenv("ENABLE_QAAPI") == "true",
                disableRateLimit = System.getenv("DISABLE_RATE_LIMIT") == "true",
            ),
            directory = Path(PathProvider.DIRECTORY_NAME),
        )
    ).start(wait = true)
}
