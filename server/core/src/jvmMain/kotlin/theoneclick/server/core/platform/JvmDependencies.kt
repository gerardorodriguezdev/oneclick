package theoneclick.server.core.platform

import theoneclick.server.core.models.Path
import theoneclick.server.core.platform.base.Dependencies
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

class JvmDependencies(
    override val environment: Environment,
    val directory: Path,
) : Dependencies {
    private val jvmSecureRandomProvider = RealJvmSecureRandomProvider()

    override val timeProvider: TimeProvider = SystemTimeProvider()
    override val uuidProvider: UuidProvider = RealUuidProvider()
    override val ivGenerator: IvGenerator = JvmIvGenerator(jvmSecureRandomProvider)
    override val securityUtils: SecurityUtils = JvmSecurityUtils(
        secretEncryptionKey = environment.secretEncryptionKey,
        jvmSecureRandomProvider = jvmSecureRandomProvider,
        timeProvider = timeProvider,
    )
    override val fileSystem: FileSystem = JvmFileSystem()
    override val pathProvider: PathProvider = PathProvider(
        directory = directory,
        fileSystem = fileSystem,
    )
}
