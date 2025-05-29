package theoneclick.client.app.application

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import io.ktor.http.*
import theoneclick.client.app.buildkonfig.BuildKonfig
import theoneclick.client.app.dataSources.AndroidEncryptedPreferences
import theoneclick.client.app.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.app.dataSources.EncryptedPreferences
import theoneclick.client.app.entrypoint.AppEntrypoint
import theoneclick.client.app.mappers.urlProtocol
import theoneclick.client.app.navigation.DefaultNavigationController
import theoneclick.client.app.platform.AndroidAppDependencies
import theoneclick.client.app.platform.AndroidLogoutManager
import theoneclick.client.app.platform.androidHttpClientEngine
import theoneclick.client.app.security.AndroidEncryptor
import theoneclick.shared.core.platform.EmptyAppLogger
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.dispatchers.platform.dispatchersProvider
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
            encryptor = AndroidEncryptor(appLogger),
        )
        val tokenDataSource = AndroidLocalTokenDataSource(encryptedPreferences)
        val navigationController = DefaultNavigationController(appLogger)
        val appDependencies = AndroidAppDependencies(
            appLogger = appLogger,
            httpClientEngine = androidHttpClientEngine(timeProvider = SystemTimeProvider()),
            tokenDataSource = tokenDataSource,
            dispatchersProvider = dispatchersProvider(),
            navigationController = navigationController,
            logoutManager = AndroidLogoutManager(
                appLogger = appLogger,
                navigationController = navigationController,
                tokenDataSource = tokenDataSource,
            )
        )
        appEntrypoint = AppEntrypoint(appDependencies)
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
