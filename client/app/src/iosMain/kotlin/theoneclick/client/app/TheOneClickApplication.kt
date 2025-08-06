package theoneclick.client.app

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import theoneclick.client.app.buildkonfig.BuildKonfig
import theoneclick.client.app.di.createAppComponent
import theoneclick.client.app.entrypoints.AppEntrypoint
import theoneclick.client.app.mappers.urlProtocol
import theoneclick.client.shared.di.iosCoreComponent
import theoneclick.client.shared.navigation.DefaultNavigationController
import theoneclick.client.shared.network.dataSources.IOSLocalTokenDataSource
import theoneclick.client.shared.network.dataSources.IOSPreferences
import theoneclick.client.shared.network.dataSources.Preferences
import theoneclick.client.shared.network.platform.IOSLogoutManager
import theoneclick.client.shared.network.platform.iosHttpClientEngine
import theoneclick.client.shared.notifications.DefaultNotificationsController
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.logging.EmptyAppLogger
import theoneclick.shared.logging.appLogger

@OptIn(ExperimentalForeignApi::class)
internal object TheOneClickApplication {
    val appEntrypoint: AppEntrypoint

    init {
        val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
        val dispatchersProvider = dispatchersProvider()
        val preferences = IOSPreferences(
            preferencesFileProvider = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                (requireNotNull(documentDirectory).path + "/${Preferences.preferencesFileName("settings")}").toPath()
            },
            appLogger = appLogger,
            dispatchersProvider = dispatchersProvider,
        )
        val tokenDataSource = IOSLocalTokenDataSource(preferences)
        val navigationController = DefaultNavigationController()
        val coreComponent = iosCoreComponent(
            urlProtocol = BuildKonfig.urlProtocol(),
            host = BuildKonfig.HOST,
            port = BuildKonfig.PORT,
            appLogger = appLogger,
            httpClientEngine = iosHttpClientEngine(),
            tokenDataSource = tokenDataSource,
            dispatchersProvider = dispatchersProvider(),
            navigationController = navigationController,
            logoutManager = IOSLogoutManager(
                navigationController = navigationController,
                tokenDataSource = tokenDataSource,
            ),
            notificationsController = DefaultNotificationsController(),
        )
        val appComponent = createAppComponent(coreComponent)
        appEntrypoint = AppEntrypoint(appComponent = appComponent, coreComponent = coreComponent)
    }
}