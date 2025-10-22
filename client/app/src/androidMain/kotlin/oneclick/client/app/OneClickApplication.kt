package oneclick.client.app

import android.app.Application
import android.os.StrictMode
import io.ktor.http.*
import oneclick.client.app.buildkonfig.BuildKonfig
import oneclick.client.app.di.createAppComponent
import oneclick.client.app.entrypoints.AppEntrypoint
import oneclick.client.app.mappers.urlProtocol
import oneclick.client.shared.di.androidCoreComponent
import oneclick.client.shared.navigation.DefaultNavigationController
import oneclick.client.shared.network.dataSources.DataStoreEncryptedPreferences
import oneclick.client.shared.network.platform.androidHttpClientEngine
import oneclick.client.shared.notifications.DefaultNotificationsController
import oneclick.shared.dispatchers.platform.dispatchersProvider
import oneclick.shared.logging.EmptyAppLogger
import oneclick.shared.logging.appLogger
import oneclick.client.shared.network.dataSources.LocalTokenDataSource
import oneclick.shared.security.DefaultSecureRandomProvider
import oneclick.shared.security.encryption.AndroidKeystoreEncryptor
import oneclick.shared.timeProvider.SystemTimeProvider

class OneClickApplication : Application() {
    lateinit var appEntrypoint: AppEntrypoint
        private set

    override fun onCreate() {
        setupStrictThreadPolicy()
        setupStrictVmPolicy()
        super.onCreate()

        val secureRandomProvider = DefaultSecureRandomProvider()
        val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
        val dispatchersProvider = dispatchersProvider()
        val encryptedPreferences = DataStoreEncryptedPreferences(
            preferencesFileProvider = {
                filesDir.resolve("settings.preferences_pb")
            },
            appLogger = appLogger,
            dispatchersProvider = dispatchersProvider,
            encryptor = AndroidKeystoreEncryptor(secureRandomProvider),
        )
        val tokenDataSource = LocalTokenDataSource(encryptedPreferences)
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
            StrictMode.ThreadPolicy.Builder()
                .detections()
                .penalties()
                .build()
        )
    }

    private fun StrictMode.ThreadPolicy.Builder.detections(): StrictMode.ThreadPolicy.Builder =
        apply {
            detectDiskWrites()
            detectCustomSlowCalls()
            detectResourceMismatches()
            detectUnbufferedIo()
            // detectDiskReads() | Required to be disabled
        }

    private fun StrictMode.ThreadPolicy.Builder.penalties(): StrictMode.ThreadPolicy.Builder =
        apply {
            penaltyLog()
            if (BuildKonfig.IS_DEBUG) {
                penaltyDeath()
            }
        }

    private fun setupStrictVmPolicy() {
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detections()
                .penalties()
                .build()
        )
    }

    private fun StrictMode.VmPolicy.Builder.detections(): StrictMode.VmPolicy.Builder =
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

    private fun StrictMode.VmPolicy.Builder.penalties(): StrictMode.VmPolicy.Builder = apply {
        penaltyLog()

        if (BuildKonfig.IS_DEBUG) {
            penaltyDeath()
        }
    }
}