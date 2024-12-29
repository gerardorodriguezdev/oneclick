package theoneclick.client.core.extensions

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import kotlin.reflect.KClass

fun typed(type: KClass<*>): Qualifier = TypeQualifier(type)
