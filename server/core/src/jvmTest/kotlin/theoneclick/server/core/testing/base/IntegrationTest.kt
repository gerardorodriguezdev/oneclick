package theoneclick.server.core.testing.base

import theoneclick.server.core.models.Path
import kotlin.io.path.createTempDirectory

actual fun IntegrationTest.tempPath(): Path = Path(createTempDirectory().toFile().absolutePath)
