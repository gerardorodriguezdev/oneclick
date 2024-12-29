package theoneclick.server.core.platform

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import theoneclick.server.core.data.models.Path
import theoneclick.server.core.platform.base.buildModule
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import kotlin.test.Test

class JvmModulesTest : IntegrationTest() {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `GIVEN default application modules THEN instances are created correctly`() {
        buildModule(
            dependencies = JvmDependencies(
                environment = TestData.environment,
                directory = Path(""),
            )
        ).verify(extraTypes = listOf(Path::class, Environment::class, Boolean::class))
    }
}
