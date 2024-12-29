package theoneclick.server.core.testing.platform

import theoneclick.server.core.data.models.Path
import theoneclick.server.core.platform.*
import theoneclick.server.core.platform.base.Dependencies
import theoneclick.server.core.testing.fakes.FakeJvmSecureRandomProvider
import theoneclick.server.core.testing.fakes.FakeUuidProvider
import theoneclick.shared.timeProvider.TimeProvider

actual fun testDependencies(
    environment: Environment,
    timeProvider: TimeProvider,
    directory: Path,
): Dependencies = object : Dependencies {
    private val fakeJvmSecureRandomProvider = FakeJvmSecureRandomProvider()

    override val environment: Environment = environment
    override val timeProvider: TimeProvider = timeProvider
    override val uuidProvider: UuidProvider = FakeUuidProvider()
    override val ivGenerator: IvGenerator = JvmIvGenerator(fakeJvmSecureRandomProvider)
    override val securityUtils: SecurityUtils = JvmSecurityUtils(
        secretEncryptionKey = environment.secretEncryptionKey,
        jvmSecureRandomProvider = fakeJvmSecureRandomProvider,
        timeProvider = timeProvider,
    )
    override val fileSystem: FileSystem = fileSystem()
    override val pathProvider: PathProvider = PathProvider(
        directory = directory,
        fileSystem = fileSystem,
    )
}
