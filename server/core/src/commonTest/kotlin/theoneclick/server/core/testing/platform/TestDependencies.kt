package theoneclick.server.core.testing.platform

import theoneclick.server.core.data.models.Path
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.platform.base.Dependencies
import theoneclick.shared.timeProvider.TimeProvider

expect fun testDependencies(
    environment: Environment,
    timeProvider: TimeProvider,
    directory: Path,
): Dependencies
