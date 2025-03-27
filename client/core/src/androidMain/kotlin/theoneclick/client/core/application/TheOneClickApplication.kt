package theoneclick.client.core.application

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import io.ktor.http.*
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.dataSources.AndroidEncryptedPreferences
import theoneclick.client.core.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.core.dataSources.EncryptedPreferences
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.mappers.urlProtocol
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.AndroidAppDependencies
import theoneclick.client.core.platform.androidHttpClientEngine
import theoneclick.client.core.security.AndroidEncryptor
import theoneclick.shared.core.platform.EmptyAppLogger
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider

class TheOneClickApplication : Application() {
    val appEntrypoint = AppEntrypoint()

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
        val appDependencies = AndroidAppDependencies(
            appLogger = appLogger,
            httpClientEngine = androidHttpClientEngine(timeProvider = SystemTimeProvider()),
            tokenDataSource = AndroidLocalTokenDataSource(encryptedPreferences),
            dispatchersProvider = dispatchersProvider(),
            navigationController = RealNavigationController(appLogger)
        )
        appEntrypoint.startKoin(appDependencies = appDependencies)
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
