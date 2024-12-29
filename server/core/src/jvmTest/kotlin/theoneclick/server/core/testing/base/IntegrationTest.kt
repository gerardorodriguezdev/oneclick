package theoneclick.server.core.testing.base

import theoneclick.server.core.data.models.Path
import kotlin.io.path.createTempDirectory

actual fun IntegrationTest.tempPath(): Path = Path(createTempDirectory().toFile().absolutePath)
