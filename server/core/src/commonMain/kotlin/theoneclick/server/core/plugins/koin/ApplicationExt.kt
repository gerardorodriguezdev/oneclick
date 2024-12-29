package theoneclick.server.core.plugins.koin

import io.ktor.server.application.*
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Temporal fix to support Ktor 3.0.0
 */

fun Application.getKoin(): Koin =
    attributes.getOrNull(KOIN_ATTRIBUTE_KEY)?.koin ?: run {
        val defaultInstance = GlobalContext.getKoinApplicationOrNull()
            ?: error("No Koin instance started. Use install(Koin) or startKoin()")
        setKoinApplication(defaultInstance)
        attributes[KOIN_ATTRIBUTE_KEY].koin
    }

inline fun <reified T : Any> Application.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(qualifier, parameters) }

inline fun <reified T : Any> Application.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = getKoin().get<T>(qualifier, parameters)
