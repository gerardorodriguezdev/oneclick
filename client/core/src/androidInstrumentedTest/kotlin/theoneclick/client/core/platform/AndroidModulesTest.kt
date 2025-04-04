package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verifyAll
import theoneclick.client.core.dataSources.AndroidEncryptedPreferences
import theoneclick.client.core.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.core.dataSources.EncryptedPreferences
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.security.AndroidEncryptor
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.testing.extensions.generateRandomString
import theoneclick.shared.timeProvider.SystemTimeProvider

class AndroidModulesTest {

    @get:Rule
    val temporalFolder = TemporaryFolder
        .builder()
        .assureDeletion()
        .build()

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun GIVEN_defaultModules_THEN_instancesAreCreatedCorrectly() {
        val appLogger = appLogger()
        val encryptedPreferences = AndroidEncryptedPreferences(
            preferencesFileProvider = {
                val fileName = EncryptedPreferences.preferencesFileName(generateRandomString(5))
                temporalFolder.newFile(fileName)
            },
            dispatchersProvider = dispatchersProvider(),
            encryptor = AndroidEncryptor(appLogger),
            appLogger = appLogger,
        )
        val appDependencies = AndroidAppDependencies(
            appLogger = appLogger,
            httpClientEngine = androidHttpClientEngine(
                timeProvider = SystemTimeProvider(),
            ),
            tokenDataSource = AndroidLocalTokenDataSource(encryptedPreferences),
            dispatchersProvider = dispatchersProvider(),
            navigationController = RealNavigationController(appLogger),
        )
        val appEntrypoint = AppEntrypoint(appDependencies = appDependencies, skipStartKoin = true)

        appEntrypoint.koinModules.verifyAll(
            listOf(
                HttpClientEngine::class,
                HttpClientConfig::class,
            )
        )
    }
}
