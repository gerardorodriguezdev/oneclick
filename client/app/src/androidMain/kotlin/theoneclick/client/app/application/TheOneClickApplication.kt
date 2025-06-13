package theoneclick.client.app.application

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import io.ktor.http.*
import theoneclick.client.app.buildkonfig.BuildKonfig
import theoneclick.client.app.di.AppComponent
import theoneclick.client.app.di.create
import theoneclick.client.app.entrypoints.AppEntrypoint
import theoneclick.client.app.mappers.urlProtocol
import theoneclick.client.shared.di.androidCoreComponent
import theoneclick.client.shared.navigation.DefaultNavigationController
import theoneclick.client.shared.network.dataSources.AndroidEncryptedPreferences
import theoneclick.client.shared.network.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.shared.network.dataSources.EncryptedPreferences
import theoneclick.client.shared.network.platform.AndroidLogoutManager
import theoneclick.client.shared.network.platform.androidHttpClientEngine
import theoneclick.client.shared.network.security.AndroidEncryptor
import theoneclick.client.shared.notifications.DefaultNotificationsController
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.logging.EmptyAppLogger
import theoneclick.shared.logging.appLogger
import theoneclick.shared.timeProvider.SystemTimeProvider

class TheOneClickApplication : Application() {
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
                filesDir.resolve(EncryptedPreferences.preferencesFileName("settings"))
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
        val appComponent = AppComponent::class.create(coreComponent)
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
