package theoneclick.client.core.application.base

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import io.ktor.http.*
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.platform.Environment

abstract class BaseAndroidApplication(
    private val appDependencies: () -> AppDependencies,
) : Application() {

    val appEntrypoint = AppEntrypoint()

    override fun onCreate() {
        val appDependencies = appDependencies()
        setupStrictThreadPolicy(appDependencies.environment)
        setupStrictVmPolicy(appDependencies.environment)
        super.onCreate()

        appEntrypoint.startKoin(appDependencies = appDependencies)
    }

    private fun setupStrictThreadPolicy(environment: Environment) {
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detections()
                .penalties(environment)
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

    private fun ThreadPolicy.Builder.penalties(environment: Environment): ThreadPolicy.Builder =
        apply {
            penaltyLog()
            if (environment.isDebug) {
                penaltyDeath()
            }
        }

    private fun setupStrictVmPolicy(environment: Environment) {
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detections(environment)
                .penalties(environment)
                .build()
        )
    }

    private fun VmPolicy.Builder.detections(environment: Environment): VmPolicy.Builder =
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

            if (environment.protocol == URLProtocol.HTTPS) {
                detectCleartextNetwork()
            }
        }

    private fun VmPolicy.Builder.penalties(environment: Environment): VmPolicy.Builder = apply {
        penaltyLog()

        if (environment.isDebug) {
            penaltyDeath()
        }
    }
}
