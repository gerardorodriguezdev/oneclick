package oneclick.client.app

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import oneclick.client.app.buildkonfig.BuildKonfig
import oneclick.client.app.di.createAppComponent
import oneclick.client.app.entrypoints.AppEntrypoint
import oneclick.client.app.mappers.urlProtocol
import oneclick.client.shared.di.iosCoreComponent
import oneclick.client.shared.navigation.DefaultNavigationController
import oneclick.client.shared.network.dataSources.IOSLocalTokenDataSource
import oneclick.client.shared.network.dataSources.IOSPreferences
import oneclick.client.shared.network.platform.IOSLogoutManager
import oneclick.client.shared.network.platform.iosHttpClientEngine
import oneclick.client.shared.notifications.DefaultNotificationsController
import oneclick.shared.dispatchers.platform.dispatchersProvider
import oneclick.shared.logging.EmptyAppLogger
import oneclick.shared.logging.appLogger

@OptIn(ExperimentalForeignApi::class)
internal object OneClickApplication {
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
                (requireNotNull(documentDirectory).path + "/settings.preferences_pb").toPath()
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
