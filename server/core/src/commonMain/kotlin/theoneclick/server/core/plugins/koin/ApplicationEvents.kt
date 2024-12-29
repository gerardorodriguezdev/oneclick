@file:Suppress("NoNonPrivateGlobalVariables")

package theoneclick.server.core.plugins.koin

import io.ktor.events.*
import org.koin.core.KoinApplication

/**
 * Temporal fix to support Ktor 3.0.0
 */

val KoinApplicationStarted = EventDefinition<KoinApplication>()

val KoinApplicationStopPreparing = EventDefinition<KoinApplication>()

val KoinApplicationStopped = EventDefinition<KoinApplication>()
