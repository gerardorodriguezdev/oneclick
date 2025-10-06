package oneclick.client.app.application

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import io.ktor.http.*
import oneclick.client.app.buildkonfig.BuildKonfig
import oneclick.client.app.di.createAppComponent
import oneclick.client.app.entrypoints.AppEntrypoint
import oneclick.client.app.mappers.urlProtocol
import oneclick.client.shared.di.androidCoreComponent
import oneclick.client.shared.navigation.DefaultNavigationController
import oneclick.client.shared.network.dataSources.AndroidEncryptedPreferences
import oneclick.client.shared.network.dataSources.AndroidLocalTokenDataSource
import oneclick.client.shared.network.platform.AndroidLogoutManager
import oneclick.client.shared.network.platform.androidHttpClientEngine
import oneclick.client.shared.network.security.AndroidEncryptor
import oneclick.client.shared.notifications.DefaultNotificationsController
import oneclick.shared.dispatchers.platform.dispatchersProvider
import oneclick.shared.logging.EmptyAppLogger
import oneclick.shared.logging.appLogger
import oneclick.shared.timeProvider.SystemTimeProvider

class OneClickApplication : Application() {
    lateinit var appEntrypoint: AppEntrypoint
        private set

    override fun onCreate() {
        setupStrictThreadPolicy()
        setupStrictVmPolicy()
        super.onCreate()

        val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
        val dispatchersProvider = dispatchersProvider()
        val encryptedPreferences = AndroidEncryptedPreferences(
            preferencesFileProvider = {
                filesDir.resolve("settings.preferences_pb")
            },
            appLogger = appLogger,
            dispatchersProvider = dispatchersProvider,
            encryptor = AndroidEncryptor(),
        )
        val tokenDataSource = AndroidLocalTokenDataSource(encryptedPreferences)
        val navigationController = DefaultNavigationController()
        val coreComponent = androidCoreComponent(
            urlProtocol = BuildKonfig.urlProtocol(),
            host = BuildKonfig.HOST,
            port = BuildKonfig.PORT,
            appLogger = appLogger,
            httpClientEngine = androidHttpClientEngine(timeProvider = SystemTimeProvider()),
            tokenDataSource = tokenDataSource,
            dispatchersProvider = dispatchersProvider(),
            navigationController = navigationController,
            logoutManager = AndroidLogoutManager(
                navigationController = navigationController,
                tokenDataSource = tokenDataSource,
            ),
            notificationsController = DefaultNotificationsController(),
        )
        val appComponent = createAppComponent(coreComponent)
        appEntrypoint = AppEntrypoint(appComponent = appComponent, coreComponent = coreComponent)
    }

    private fun setupStrictThreadPolicy() {
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detections()
                .penalties()
                .build()
        )
    }

    private fun ThreadPolicy.Builder.detections(): ThreadPolicy.Builder =
        apply {
            detectDiskWrites()
            detectCustomSlowCalls()
            detectResourceMismatches()
            detectUnbufferedIo()
            // detectDiskReads() | Required to be disabled
        }

    private fun ThreadPolicy.Builder.penalties(): ThreadPolicy.Builder =
        apply {
            penaltyLog()
            if (BuildKonfig.IS_DEBUG) {
                penaltyDeath()
            }
        }

    private fun setupStrictVmPolicy() {
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detections()
                .penalties()
                .build()
        )
    }

    private fun VmPolicy.Builder.detections(): VmPolicy.Builder =
        apply {
            detectUnsafeIntentLaunch()
            detectUntaggedSockets()
            detectCredentialProtectedWhileLocked()
            detectContentUriWithoutPermission()
            detectActivityLeaks()
            detectFileUriExposure()
            detectImplicitDirectBoot()
            detectIncorrectContextUse()
            detectLeakedRegistrationObjects()
            detectLeakedSqlLiteObjects()
            // detectLeakedClosableObjects() | Required to be disabled. Related to keyboard

            if (BuildKonfig.urlProtocol() == URLProtocol.HTTPS) {
                detectCleartextNetwork()
            }
        }

    private fun VmPolicy.Builder.penalties(): VmPolicy.Builder = apply {
        penaltyLog()

        if (BuildKonfig.IS_DEBUG) {
            penaltyDeath()
        }
    }
}
